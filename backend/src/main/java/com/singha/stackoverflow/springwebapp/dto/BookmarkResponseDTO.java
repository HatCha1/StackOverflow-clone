package com.singha.stackoverflow.springwebapp.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class BookmarkResponseDTO {
    private Long userId;
    private Long questionId;
    private String questionTitle;
    private LocalDateTime createdAt;
}