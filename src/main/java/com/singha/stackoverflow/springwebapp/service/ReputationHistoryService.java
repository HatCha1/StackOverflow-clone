package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.dto.ReputationHistoryResponse;
import com.singha.stackoverflow.springwebapp.entity.Answer;
import com.singha.stackoverflow.springwebapp.entity.Question;
import com.singha.stackoverflow.springwebapp.entity.ReputationHistory;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.enums.VoteType;
import com.singha.stackoverflow.springwebapp.exception.BadRequestException;
import com.singha.stackoverflow.springwebapp.exception.ResourceNotFoundException;
import com.singha.stackoverflow.springwebapp.repository.AnswerRepository;
import com.singha.stackoverflow.springwebapp.repository.QuestionRepository;
import com.singha.stackoverflow.springwebapp.repository.ReputationHistoryRepository;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ReputationHistoryService {

    @Autowired
    private ReputationHistoryRepository reputationHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    public void applyQuestionVoteReputation(User questionOwner, Long questionId, VoteType oldVoteType, VoteType newVoteType) {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                        .getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new ResourceNotFoundException("Question not found"));

        int change = calculateQuestionReputationChange(oldVoteType, newVoteType);

        if (change == 0 && oldVoteType != newVoteType) {
            throw new BadRequestException("Reputation change can't be zero");
        }

        createReputationHistory(
                questionOwner.getUserId(),       // receiver
                currentUser.getUserId(),       // voter/source user
                question.getQuestionId(),
                null,
                change,
                "Answer upvoted"
        );
    }

    public void applyAnswerVoteReputation(User answerOwner, Long answerId, VoteType oldVoteType, VoteType newVoteType) {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                .getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Answer answer = answerRepository.findById(answerId).orElseThrow(
                () -> new ResourceNotFoundException("Answer not found"));

        int change = calculateAnswerReputationChange(oldVoteType, newVoteType);

        if (change == 0 && oldVoteType != newVoteType) {
            throw new BadRequestException("Reputation change can't be zero");
        }

        createReputationHistory(
                answerOwner.getUserId(),       // receiver
                currentUser.getUserId(),       // voter/source user
                null,
                answer.getAnswerId(),
                change,
                "Answer upvoted"
        );
    }

    private int calculateQuestionReputationChange(VoteType oldVoteType, VoteType newVoteType) {
        int oldValue = getQuestionVoteValue(oldVoteType);
        int newValue = getQuestionVoteValue(newVoteType);

        return newValue - oldValue;
    }

    private int calculateAnswerReputationChange(VoteType oldVoteType, VoteType newVoteType) {
        int oldValue = getAnswerVoteValue(oldVoteType);
        int newValue = getAnswerVoteValue(newVoteType);

        return newValue - oldValue;
    }

    private int getQuestionVoteValue(VoteType voteType) {
        if (voteType == null) {
            throw new BadRequestException("Vote type can't be null");
        }

        return switch (voteType) {
            case UPVOTE -> 5;
            case DOWNVOTE -> -2;
        };
    }

    private int getAnswerVoteValue(VoteType voteType) {
        if (voteType == null) {
            throw new BadRequestException("Vote type can't be null");
        }

        return switch (voteType) {
            case UPVOTE -> 10;
            case DOWNVOTE -> -2;
        };
    }

    public void createReputationHistory(
            Long userId,
            Long sourceUserId,
            Long questionId,
            Long answerId,
            Integer reputationChange,
            String reason
    ) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );

        User sourceUser = null;
        if (sourceUserId != null) {
            sourceUser = userRepository.findById(sourceUserId).orElseThrow(
                    () -> new ResourceNotFoundException("Source user not found")
            );
        }

        Question question = null;
        if (questionId != null) {
            question = questionRepository.findById(questionId).orElseThrow(
                    () -> new ResourceNotFoundException("Question not found")
            );
        }

        Answer answer = null;
        if (answerId != null) {
            answer = answerRepository.findById(answerId).orElseThrow(
                    () -> new ResourceNotFoundException("Answer not found")
            );
        }

        user.setReputation(user.getReputation() + reputationChange);

        userRepository.save(user);

        reputationHistoryRepository.save(
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

    public List<ReputationHistoryResponse> getReputationHistoryByUserId() {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                .getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<ReputationHistory> histories =
                reputationHistoryRepository.findByUser_UserIdOrderByCreatedAtDesc(currentUser.getUserId());

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
