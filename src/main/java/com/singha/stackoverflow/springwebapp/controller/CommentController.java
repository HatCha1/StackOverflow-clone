package com.singha.stackoverflow.springwebapp.controller;

import com.singha.stackoverflow.springwebapp.dto.QuestionCommentCreateDTO;
import com.singha.stackoverflow.springwebapp.dto.AnswerCommentCreateDTO;
import com.singha.stackoverflow.springwebapp.dto.CommentResponseDTO;
import com.singha.stackoverflow.springwebapp.entity.Comment;
import com.singha.stackoverflow.springwebapp.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 1. Post a comment to a Question thread
    @PostMapping("/question")
    public ResponseEntity<CommentResponseDTO> addCommentToQuestion(@RequestBody QuestionCommentCreateDTO createDTO) {
        Comment savedComment = commentService.createQuestionComment(
                createDTO.getBody(),
                createDTO.getUserId(),
                createDTO.getQuestionId()
        );
        return ResponseEntity.ok(convertToResponseDTO(savedComment));
    }

    // 2. Post a comment to an Answer block
    @PostMapping("/answer")
    public ResponseEntity<CommentResponseDTO> addCommentToAnswer(@RequestBody AnswerCommentCreateDTO createDTO) {
        Comment savedComment = commentService.createAnswerComment(
                createDTO.getBody(),
                createDTO.getUserId(),
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
}