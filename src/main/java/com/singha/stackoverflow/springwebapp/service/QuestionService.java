package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.entity.Question;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.repository.QuestionRepository;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    public Question createQuestion(String title, String body, Long userId) {
        // 1. Fetch the user object to establish the relationship
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 2. Build the entity
        Question question = new Question();
        question.setTitle(title);
        question.setBody(body);
        question.setUser(author); // Maps the foreign key constraint
        question.setCreatedAt(LocalDateTime.now()); // Satisfies the not-null column

        return questionRepository.save(question);
    }

    // READ ALL
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // READ ONE
    public Optional<Question> getQuestionById(Long questionId) {
        return questionRepository.findById(questionId);
    }

    // UPDATE
    public Question updateQuestion(Long questionId, Long userId, String newTitle, String newBody) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // Validation Check: Does this question belong to the requesting user?
        if (!question.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to edit this question!");
        }

        question.setTitle(newTitle);
        question.setBody(newBody);

        return questionRepository.save(question);
    }

    // DELETE
    public void deleteQuestion(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // Validation Check
        if (!question.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to delete this question!");
        }

        questionRepository.delete(question);
    }

}