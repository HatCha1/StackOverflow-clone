package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.dto.AnswerResponse;
import com.singha.stackoverflow.springwebapp.entity.Answer;
import com.singha.stackoverflow.springwebapp.entity.Question;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.enums.VoteType;
import com.singha.stackoverflow.springwebapp.exception.ResourceNotFoundException;
import com.singha.stackoverflow.springwebapp.exception.UnauthorizedException;
import com.singha.stackoverflow.springwebapp.repository.AnswerRepository;
import com.singha.stackoverflow.springwebapp.repository.QuestionRepository;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import com.singha.stackoverflow.springwebapp.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class AnswerService {

        @Autowired
        private AnswerRepository answerRepository;

        @Autowired
        private QuestionRepository questionRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private BadgeService badgeService;

        @Autowired
        private VoteRepository voteRepository;

        @Transactional
        public AnswerResponse createAnswer(Long questionId, String body) {

                String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                                .getAuthentication())
                        .getName();

                User currentUser = userRepository.findByUsername(username)
                        .or(() -> userRepository.findByEmail(username))
                        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

                Question question = questionRepository.findById(questionId).orElseThrow(
                                () -> new ResourceNotFoundException("Question not found"));

                Answer answer = new Answer();
                answer.setQuestion(question);
                answer.setUser(currentUser);
                answer.setBody(body);
                answer.setIsAccepted(false);
                answer.setCreatedAt(LocalDateTime.now());
                answer.setUpdatedAt(LocalDateTime.now());

                Answer savedAnswer = answerRepository.save(answer);

                badgeService.checkBadges();

                return convertToResponse(savedAnswer);
        }

        public List<AnswerResponse> getAnswersByQuestionId(Long questionId) {
                List<Answer> answers = answerRepository.findByQuestion_QuestionIdOrderByCreatedAtAsc(questionId);

                return answers.stream()
                                .map(this::convertToResponse)
                                .toList();
        }

        public List<AnswerResponse> getAnswersByUserId(Long userId) {
                List<Answer> answers = answerRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);

                return answers.stream()
                                .map(this::convertToResponse)
                                .toList();
        }

        public AnswerResponse getAnswerById(Long answerId) {
                Answer answer = answerRepository.findById(answerId).orElseThrow(
                                () -> new ResourceNotFoundException("Answer not found"));

                return convertToResponse(answer);
        }

        @Transactional
        public AnswerResponse updateAnswer(Long answerId, String body) {

                String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                                .getAuthentication())
                                .getName();

                User currentUser = userRepository.findByUsername(username)
                        .or(() -> userRepository.findByEmail(username))
                        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

                Answer answer = answerRepository.findById(answerId).orElseThrow(
                                () -> new ResourceNotFoundException("Answer not found"));

                if (!answer.getUser().getUserId().equals(currentUser.getUserId())) {
                        throw new UnauthorizedException("You are not authorized to edit this answer");
                }

                answer.setBody(body);
                answer.setUpdatedAt(LocalDateTime.now());

                Answer updatedAnswer = answerRepository.save(answer);

                return convertToResponse(updatedAnswer);
        }

        @Transactional
        public void deleteAnswer(Long answerId) {
                String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                                .getAuthentication())
                                .getName();

                User currentUser = userRepository.findByUsername(username)
                        .or(() -> userRepository.findByEmail(username))
                        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

                Answer answer = answerRepository.findById(answerId).orElseThrow(
                                () -> new ResourceNotFoundException("Answer not found"));

                if (!answer.getUser().getUserId().equals(currentUser.getUserId())) {
                        throw new UnauthorizedException("You are not authorized to delete this answer");
                }

                answerRepository.delete(answer);
        }

        @Transactional
        public AnswerResponse acceptAnswer(Long answerId) {
                String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                                .getAuthentication())
                                .getName();

                User currentUser = userRepository.findByUsername(username)
                        .or(() -> userRepository.findByEmail(username))
                        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

                Answer answer = answerRepository.findById(answerId).orElseThrow(
                                () -> new ResourceNotFoundException("Answer not found"));

                Question question = answer.getQuestion();

                if (!question.getUser().getUserId().equals(currentUser.getUserId())) {
                        throw new UnauthorizedException("You are not authorized to accept this answer");
                }

                if (answer.getIsAccepted() == true) {
                        answer.setIsAccepted(false);
                        answer.setUpdatedAt(LocalDateTime.now());

                        Answer acceptedAnswer = answerRepository.save(answer);

                        return convertToResponse(acceptedAnswer);
                }
                answer.setIsAccepted(true);
                answer.setUpdatedAt(LocalDateTime.now());

                Answer acceptedAnswer = answerRepository.save(answer);

                return convertToResponse(acceptedAnswer);
        }

        private AnswerResponse convertToResponse(Answer answer) {
                long upvotes   = voteRepository.countByAnswer_AnswerIdAndVoteType(answer.getAnswerId(), VoteType.UPVOTE);
                long downvotes = voteRepository.countByAnswer_AnswerIdAndVoteType(answer.getAnswerId(), VoteType.DOWNVOTE);

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
                                .voteCount((int)(upvotes - downvotes))
                                .build();
        }
}