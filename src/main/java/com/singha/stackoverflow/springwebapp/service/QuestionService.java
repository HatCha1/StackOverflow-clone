package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.entity.Question;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.exception.ResourceNotFoundException;
import com.singha.stackoverflow.springwebapp.exception.UnauthorizedException;
import com.singha.stackoverflow.springwebapp.repository.QuestionRepository;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public Question createQuestion(String title, String body, String username) {
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

        return savedQuestion;
    }

    // READ ALL
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // READ ONE
    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId).orElseThrow(
                () -> new ResourceNotFoundException("Question not found")
        );
    }

    // UPDATE
    public Question updateQuestion(Long questionId, String username, String newTitle, String newBody) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        // Verify the JWT-authenticated user actually owns this question
        if (!question.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not authorized to edit this question!");
        }

        question.setTitle(newTitle);
        question.setBody(newBody);

        return questionRepository.save(question);
    }

    // DELETE
    public void deleteQuestion(Long questionId, String username) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        // Verify the JWT-authenticated user actually owns this question
        if (!question.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not authorized to delete this question!");
        }

        questionRepository.delete(question);
    }
}