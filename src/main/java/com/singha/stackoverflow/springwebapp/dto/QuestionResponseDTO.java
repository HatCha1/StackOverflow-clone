package com.singha.stackoverflow.springwebapp.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionResponseDTO {
    private Long questionId;
    private String title;
    private String body;
    private Integer viewCount;
    private Boolean isClosed;
    private LocalDateTime createdAt;
    private String authorName; // Flattened field to send back the user's name cleanly
    private Integer voteCount;
    private List<String> tags;
}