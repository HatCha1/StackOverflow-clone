package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.dto.QuestionResponseDTO;
import com.singha.stackoverflow.springwebapp.entity.Question;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.enums.VoteType;
import com.singha.stackoverflow.springwebapp.exception.ResourceNotFoundException;
import com.singha.stackoverflow.springwebapp.exception.UnauthorizedException;
import com.singha.stackoverflow.springwebapp.repository.QuestionRepository;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import com.singha.stackoverflow.springwebapp.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private VoteRepository voteRepository;

    @Transactional
    public QuestionResponseDTO createQuestion(String title, String body, String username) {
        // Resolve the authenticated user from their username (extracted from JWT by the controller)
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Question question = new Question();
        question.setTitle(title);
        question.setBody(body);
        question.setUser(author);
        question.setCreatedAt(LocalDateTime.now());

        Question savedQuestion = questionRepository.save(question);

        badgeService.checkBadges();

        return toDTO(savedQuestion);
    }

    // READ ALL
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getAllQuestions() {
        return questionRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // READ ONE
    @Transactional(readOnly = true)
    public QuestionResponseDTO getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
    }

    // UPDATE
    @Transactional
    public QuestionResponseDTO updateQuestion(Long questionId, String username, String newTitle, String newBody) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        // Verify the JWT-authenticated user actually owns this question
        if (!question.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not authorized to edit this question!");
        }

        question.setTitle(newTitle);
        question.setBody(newBody);
        question.setUpdatedAt(LocalDateTime.now());

        return toDTO(questionRepository.save(question));
    }

    // DELETE
    @Transactional
    public void deleteQuestion(Long questionId, String username) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        // Verify the JWT-authenticated user actually owns this question
        if (!question.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not authorized to delete this question!");
        }

        questionRepository.delete(question);
    }

    // Private mapping helper — single source of truth for Question → DTO conversion
    private QuestionResponseDTO toDTO(Question question) {
        long upvotes   = voteRepository.countByQuestion_QuestionIdAndVoteType(question.getQuestionId(), VoteType.UPVOTE);
        long downvotes = voteRepository.countByQuestion_QuestionIdAndVoteType(question.getQuestionId(), VoteType.DOWNVOTE);

        QuestionResponseDTO dto = new QuestionResponseDTO();
        dto.setQuestionId(question.getQuestionId());
        dto.setTitle(question.getTitle());
        dto.setBody(question.getBody());
        dto.setViewCount(question.getViewCount());
        dto.setIsClosed(question.getIsClosed());
        dto.setCreatedAt(question.getCreatedAt());
        dto.setAuthorName(question.getUser().getUsername());
        dto.setVoteCount((int)(upvotes - downvotes));
        return dto;
    }
}