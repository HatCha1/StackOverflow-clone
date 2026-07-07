package com.singha.stackoverflow.springwebapp.controller;

import com.singha.stackoverflow.springwebapp.dto.CommentUpdateDTO;
import com.singha.stackoverflow.springwebapp.dto.QuestionCommentCreateDTO;
import com.singha.stackoverflow.springwebapp.dto.AnswerCommentCreateDTO;
import com.singha.stackoverflow.springwebapp.dto.CommentResponseDTO;
import com.singha.stackoverflow.springwebapp.entity.Comment;
import com.singha.stackoverflow.springwebapp.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 1. Post a comment to a Question thread
    @PostMapping("/question")
    public ResponseEntity<CommentResponseDTO> addCommentToQuestion(@Valid @RequestBody QuestionCommentCreateDTO createDTO) {
        Comment savedComment = commentService.createQuestionComment(
                createDTO.getBody(),
                createDTO.getQuestionId()
        );
        return ResponseEntity.ok(convertToResponseDTO(savedComment));
    }

    // 2. Post a comment to an Answer block
    @PostMapping("/answer")
    public ResponseEntity<CommentResponseDTO> addCommentToAnswer(@Valid @RequestBody AnswerCommentCreateDTO createDTO) {
        Comment savedComment = commentService.createAnswerComment(
                createDTO.getBody(),
                createDTO.getAnswerId()
        );
        return ResponseEntity.ok(convertToResponseDTO(savedComment));
    }

    // Helper conversion mapping method
    private CommentResponseDTO convertToResponseDTO(Comment comment) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setCommentId(comment.getCommentId());
        dto.setBody(comment.getBody());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUserId(comment.getUser().getUserId());
        dto.setAuthorName(comment.getUser().getUsername());

        // Populate IDs safely depending on what exists
        if (comment.getQuestion() != null) {
            dto.setQuestionId(comment.getQuestion().getQuestionId());
        }
        if (comment.getAnswer() != null) {
            dto.setAnswerId(comment.getAnswer().getAnswerId());
        }
        return dto;
    }

    // 1. GET ALL comments for a specific question
    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByQuestion(@PathVariable Long questionId) {
        List<Comment> comments = commentService.getCommentsByQuestionId(questionId);

        // Map the whole list of entities cleanly into a list of flat Response DTOs
        List<CommentResponseDTO> response = comments.stream()
                .map(this::convertToResponseDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    // 2. GET ONE specific comment by its ID
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable Long commentId) {
        Comment comment = commentService.getCommentById(commentId);

        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(convertToResponseDTO(comment));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateDTO updateDTO) {

        Comment updatedComment = commentService.updateComment(
                commentId,
                updateDTO.getBody()
        );
        return ResponseEntity.ok(convertToResponseDTO(updatedComment));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok("Comment deleted successfully.");
    }
}