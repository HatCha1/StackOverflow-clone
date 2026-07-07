package com.singha.stackoverflow.springwebapp.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentResponseDTO {
    private Long commentId;
    private String body;
    private LocalDateTime createdAt;

    // Flattened relational fields
    private Long userId;
    private String authorName;

    // One of these will be populated, the other will naturally be null
    private Long questionId;
    private Long answerId;
}