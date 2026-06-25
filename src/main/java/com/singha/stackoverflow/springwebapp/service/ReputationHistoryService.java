package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.dto.ReputationHistoryResponse;
import com.singha.stackoverflow.springwebapp.entity.ReputationHistory;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.repository.ReputationHistoryRepository;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReputationHistoryService {

    @Autowired
    private ReputationHistoryRepository reputationHistoryRepository;
    private UserRepository userRepository;
    private QuestionRepository questionRepository;
    private AnswerRepository answerRepository;

    public ReputationHistory createReputationHistory(
            Long userId,
            Long sourceUserId,
            Long questionId,
            Long answerId,
            Integer reputationChange,
            String reason
    ) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        User sourceUser = null;
        if (sourceUserId != null) {
            sourceUser = userRepository.findById(sourceUserId).orElseThrow(
                    () -> new RuntimeException("Source user not found")
            );
        }

        Question question = null;
        if (questionId != null) {
            question = questionRepository.findById(questionId).orElseThrow(
                    () -> new RuntimeException("Question not found")
            );
        }

        Answer answer = null;
        if (answerId != null) {
            answer = answerRepository.findById(answerId).orElseThrow(
                    () -> new RuntimeException("Answer not found")
            );
        }

        return reputationHistoryRepository.save(
                ReputationHistory.builder()
                .user(user)
                .sourceUser(sourceUser)
                .question(question)
                .answer(answer)
                .reputationChange(reputationChange)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build()
                );
    }

    public List<ReputationHistoryResponse> getReputationHistoryByUserId(Long userId) {
        List<ReputationHistory> histories =
                reputationHistoryRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);

        return histories.stream() // Just like rust .iter().map()
                .map(this::convertToResponse)
                .toList();
    }

    private ReputationHistoryResponse convertToResponse(ReputationHistory history) {
        return ReputationHistoryResponse.builder()
                .reputationId(history.getReputationId())

                .userId(history.getUser().getUserId())
                .username(history.getUser().getUsername())

                .sourceUserId(history.getSourceUser() != null
                        ? history.getSourceUser().getUserId()
                        : null)
                .sourceUsername(history.getSourceUser() != null
                        ? history.getSourceUser().getUsername()
                        : null)

                .questionId(history.getQuestion() != null
                        ? history.getQuestion().getQuestionId()
                        : null)
                .answerId(history.getAnswer() != null
                        ? history.getAnswer().getAnswerId()
                        : null)

                .reputationChange(history.getReputationChange())
                .reason(history.getReason())
                .createdAt(history.getCreatedAt())
                .build();
    }
}
