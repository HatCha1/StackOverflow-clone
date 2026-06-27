package com.singha.stackoverflow.springwebapp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeResponse {

    private Long badgeId;

    private String badgeName;

    private String description;

    private String badgeType;
}