package com.singha.stackoverflow.springwebapp.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReputationHistoryResponse {

    private Long reputationId;

    private Long userId;
    private String username;

    private Long sourceUserId;
    private String sourceUsername;

    private Long questionId;
    private Long answerId;

    private Integer reputationChange;
    private String reason;

    private LocalDateTime createdAt;
}