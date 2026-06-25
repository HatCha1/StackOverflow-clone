package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.dto.AnswerResponse;
import com.singha.stackoverflow.springwebapp.entity.Answer;
import com.singha.stackoverflow.springwebapp.entity.Question;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.repository.AnswerRepository;
import com.singha.stackoverflow.springwebapp.repository.QuestionRepository;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    public AnswerResponse createAnswer(Long questionId, Long userId, String body) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new RuntimeException("Question not found")
        );

        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setUser(user);
        answer.setBody(body);
        answer.setIsAccepted(false);
        answer.setCreatedAt(LocalDateTime.now());
        answer.setUpdatedAt(LocalDateTime.now());

        return convertToResponse(answerRepository.save(answer));
    }

    public List<AnswerResponse> getAnswersByQuestionId(Long questionId) {
        List<Answer> answers =
                answerRepository.findByQuestion_QuesetionIdOrderByCreatdAtAsc(questionId);

        return answers.stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<AnswerResponse> getAnswersByUserId(Long userId) {
        List<Answer> answers =
                answerRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);

        return answers.stream()
                .map(this::convertToResponse)
                .toList();
    }

    public AnswerResponse getAnswerById(Long answerId) {
        Answer answer = answerRepository.findById(answerId).orElseThrow(
                () -> new RuntimeException("Answer not found")
        );

        return convertToResponse(answer);
    }

    public AnswerResponse updateAnswer(Long answerId, String body) {
        Answer answer = answerRepository.findById(answerId).orElseThrow(
                () -> new RuntimeException("Answer not found")
        );

        answer.setBody(body);
        answer.setUpdatedAt(LocalDateTime.now());

        Answer updatedAnswer = answerRepository.save(answer);

        return convertToResponse(updatedAnswer);
    }

    public void deleteAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId).orElseThrow(
                () -> new RuntimeException("Answer not found")
        );

        answerRepository.delete(answer);
    }

    public AnswerResponse acceptAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId).orElseThrow(
                () -> new RuntimeException("Answer not found")
        );

        answer.setIsAccepted(true);
        answer.setUpdatedAt(LocalDateTime.now());

        Answer acceptedAnswer = answerRepository.save(answer);

        return convertToResponse(acceptedAnswer);
    }

    private AnswerResponse convertToResponse(Answer answer) {
        return AnswerResponse.builder()
                .answerId(answer.getAnswerId())

                .questionId(answer.getQuestion().getQuestionId())

                .userId(answer.getUser().getUserId())
                .username(answer.getUser().getUsername())
                .displayName(answer.getUser().getDisplayName())

                .body(answer.getBody())
                .isAccepted(answer.getIsAccepted())
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .build();
    }
}