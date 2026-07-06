package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.dto.QuestionResponseDTO;
import com.singha.stackoverflow.springwebapp.entity.Question;
import com.singha.stackoverflow.springwebapp.entity.Tag;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.enums.VoteType;
import com.singha.stackoverflow.springwebapp.exception.ResourceNotFoundException;
import com.singha.stackoverflow.springwebapp.exception.UnauthorizedException;
import com.singha.stackoverflow.springwebapp.repository.QuestionRepository;
import com.singha.stackoverflow.springwebapp.repository.TagRepository;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import com.singha.stackoverflow.springwebapp.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private TagRepository tagRepository;

    @Transactional
    public QuestionResponseDTO createQuestion(String title, String body, String username, List<String> tags) {
        // Resolve the authenticated user from their username (extracted from JWT by the controller)
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Set<Tag> questionTags = tags.stream()
                .map(String::trim)
                .filter(tagName -> !tagName.isBlank())
                .map(tagName -> tagRepository.findByTagNameIgnoreCase(tagName)
                        .orElseGet(() -> {
                            Tag tag = new Tag();
                            tag.setTagName(tagName.toLowerCase());
                            return tagRepository.save(tag);
                        }))
                .collect(Collectors.toSet());

        Question question = new Question();
        question.setTitle(title);
        question.setBody(body);
        question.setUser(author);
        question.setCreatedAt(LocalDateTime.now());
        question.setTags(questionTags);

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
    @Transactional
    public QuestionResponseDTO getQuestionById(Long questionId, String username) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if(!Objects.equals(question.getUser().getUserId(), author.getUserId())) {
            question.setViewCount(question.getViewCount() + 1);
        }

        return toDTO(question);
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
        dto.setTags(
                question.getTags()
                        .stream()
                        .map(Tag::getTagName)
                        .toList()
        );
        return dto;
    }
}