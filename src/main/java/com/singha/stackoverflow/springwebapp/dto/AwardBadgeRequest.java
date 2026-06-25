package com.singha.stackoverflow.springwebapp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AwardBadgeRequest {

    private Long userId;

    private Long badgeId;
}