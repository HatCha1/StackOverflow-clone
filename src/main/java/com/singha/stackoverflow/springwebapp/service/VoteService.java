package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.dto.VoteRequestDTO;
import com.singha.stackoverflow.springwebapp.dto.VoteResponseDTO;
import com.singha.stackoverflow.springwebapp.entity.Answer;
import com.singha.stackoverflow.springwebapp.entity.Question;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.entity.Vote;
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

    public VoteResponseDTO  voteQuestion(Long questionId, VoteRequestDTO request) {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                        .getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        Optional<Vote> existingVote = voteRepository.findByUser_UserIdAndQuestion_QuestionId(currentUser.getUserId(), questionId);

        Vote vote;
        if (existingVote.isPresent()) {
            vote = existingVote.get();
            vote.setVoteType(request.getVoteType());
        } else {
            vote = Vote.builder()
                    .user(currentUser)
                    .question(question)
                    .voteType(request.getVoteType())
                    .build();
        }

        return convertToResponse(voteRepository.save(vote));
    }

    public VoteResponseDTO voteAnswer(Long answerId, VoteRequestDTO request) {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                .getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));

        Optional<Vote> existingVote = voteRepository.findByUser_UserIdAndAnswer_AnswerId(currentUser.getUserId(), answerId);

        Vote vote;
        if (existingVote.isPresent()) {
            vote = existingVote.get();
            vote.setVoteType(request.getVoteType());
        } else {
            vote = Vote.builder()
                    .user(currentUser)
                    .answer(answer)
                    .voteType(request.getVoteType())
                    .build();
        }

        return convertToResponse(voteRepository.save(vote));
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