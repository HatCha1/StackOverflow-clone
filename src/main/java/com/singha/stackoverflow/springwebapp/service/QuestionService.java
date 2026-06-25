package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.entity.Question;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.repository.QuestionRepository;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

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
}