package com.singha.stackoverflow.springwebapp.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerResponse {

    private Long answerId;

    private Long questionId;

    private Long userId;
    private String username;
    private String displayName;

    private String body;
    private Boolean isAccepted;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}