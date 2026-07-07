package com.singha.stackoverflow.springwebapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AwardBadgeRequest {

    @NotBlank(message = "User ID can not be blank")
    private Long userId;

    @NotBlank(message = "Badge ID can not be blank")
    private Long badgeId;
}