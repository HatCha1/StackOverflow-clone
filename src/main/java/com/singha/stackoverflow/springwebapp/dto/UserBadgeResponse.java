package com.singha.stackoverflow.springwebapp.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBadgeResponse {

    private Long userId;
    private String username;

    private Long badgeId;
    private String badgeName;
    private String badgeType;
    private String description;

    private LocalDateTime awardedAt;
}