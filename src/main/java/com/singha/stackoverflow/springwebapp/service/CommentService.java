package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.entity.Answer;
import com.singha.stackoverflow.springwebapp.entity.Comment;
import com.singha.stackoverflow.springwebapp.entity.Question;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.exception.ResourceNotFoundException;
import com.singha.stackoverflow.springwebapp.exception.UnauthorizedException;
import com.singha.stackoverflow.springwebapp.repository.AnswerRepository;
import com.singha.stackoverflow.springwebapp.repository.CommentRepository;
import com.singha.stackoverflow.springwebapp.repository.QuestionRepository;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

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
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));

        Comment comment = new Comment();
        comment.setBody(body);
        comment.setUser(author);
        comment.setQuestion(null);
        comment.setAnswer(answer);
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    // 1. Get all comments belonging to a specific question thread
    public List<Comment> getCommentsByQuestionId(Long questionId) {
        return commentRepository.findByQuestion_QuestionIdOrderByCreatedAtAsc(questionId);
    }

    // 2. Get a single specific comment by its unique ID
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundException("Comment not found")
        );
    }

    public Comment updateComment(Long commentId, Long userId, String newBody) {
        // 1. Fetch the existing comment
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + commentId));

        // 2. Ownership Validation: Only the author can edit it!
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to edit this comment!");
        }

        // 3. Update the mutable field
        comment.setBody(newBody);

        // 4. Flush the changes to PostgreSQL
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, Long userId) {
        // 1. Check if the comment even exists
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + commentId));

        // 2. Security/Ownership Check: Does this comment belong to the requesting user?
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this comment!");
        }

        // 3. If passed, delete from PostgreSQL
        commentRepository.delete(comment);
    }
}