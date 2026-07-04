package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.dto.VoteRequestDTO;
import com.singha.stackoverflow.springwebapp.dto.VoteResponseDTO;
import com.singha.stackoverflow.springwebapp.entity.Answer;
import com.singha.stackoverflow.springwebapp.entity.Question;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.entity.Vote;
import com.singha.stackoverflow.springwebapp.enums.VoteType;
import com.singha.stackoverflow.springwebapp.exception.BadRequestException;
import com.singha.stackoverflow.springwebapp.exception.ResourceNotFoundException;
import com.singha.stackoverflow.springwebapp.repository.AnswerRepository;
import com.singha.stackoverflow.springwebapp.repository.QuestionRepository;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import com.singha.stackoverflow.springwebapp.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private ReputationHistoryService reputationHistoryService;

    public VoteResponseDTO  voteQuestion(Long questionId, VoteType voteType) {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                        .getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        User questionOwner = question.getUser();

        if (questionOwner.getUserId().equals(currentUser.getUserId())) {
            throw new BadRequestException("You cannot vote your own question");
        }

        Optional<Vote> existingVote = voteRepository.findByUser_UserIdAndQuestion_QuestionId(currentUser.getUserId(), question.getQuestionId());

        Vote vote;
        VoteType oldVoteType = null;

        if (existingVote.isPresent()) {
            vote = existingVote.get();
            oldVoteType = vote.getVoteType();
            vote.setVoteType(voteType);
        } else {
            vote = Vote.builder()
                    .user(currentUser)
                    .question(question)
                    .voteType(voteType)
                    .build();
        }

        Vote savedVote = voteRepository.save(vote);

        reputationHistoryService.applyQuestionVoteReputation(
                questionOwner,
                question.getQuestionId(),
                oldVoteType,
                voteType
        );

        return convertToResponse(savedVote);
    }

    public VoteResponseDTO voteAnswer(Long answerId, VoteType voteType) {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                        .getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));

        User answerOwner = answer.getUser();

        if (answerOwner.getUserId().equals(currentUser.getUserId())) {
            throw new BadRequestException("You cannot vote your own answer");
        }

        Optional<Vote> existingVote = voteRepository.findByUser_UserIdAndAnswer_AnswerId(currentUser.getUserId(), answer.getAnswerId());

        Vote vote;
        VoteType oldVoteType = null;

        if (existingVote.isPresent()) {
            vote = existingVote.get();
            oldVoteType = vote.getVoteType();
            vote.setVoteType(voteType);
        } else {
            vote = Vote.builder()
                    .user(currentUser)
                    .answer(answer)
                    .voteType(voteType)
                    .build();
        }

        Vote savedVote = voteRepository.save(vote);

        reputationHistoryService.applyAnswerVoteReputation(
                answerOwner,
                answer.getAnswerId(),
                oldVoteType,
                voteType
        );

        return convertToResponse(savedVote);
    }

    private VoteResponseDTO convertToResponse(Vote vote) {
        return VoteResponseDTO.builder()
                .voteId(vote.getVoteId())
                .userId(vote.getUser().getUserId())
                .questionId(vote.getQuestion() != null ? vote.getQuestion().getQuestionId() : null)
                .answerId(vote.getAnswer() != null ? vote.getAnswer().getAnswerId() : null)
                .voteType(vote.getVoteType())
                .build();
    }
}