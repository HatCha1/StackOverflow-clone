package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.entity.Answer;
import com.singha.stackoverflow.springwebapp.entity.Comment;
import com.singha.stackoverflow.springwebapp.entity.Question;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.repository.AnswerRepository;
import com.singha.stackoverflow.springwebapp.repository.CommentRepository;
import com.singha.stackoverflow.springwebapp.repository.QuestionRepository;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    public Comment createQuestionComment(String body, Long userId, Long questionId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Comment comment = new Comment();
        comment.setBody(body);
        comment.setUser(author);
        comment.setQuestion(question);
        comment.setAnswer(null);
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    public Comment createAnswerComment(String body, Long userId, Long answerId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        Comment comment = new Comment();
        comment.setBody(body);
        comment.setUser(author);
        comment.setQuestion(null);
        comment.setAnswer(answer);
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }
}