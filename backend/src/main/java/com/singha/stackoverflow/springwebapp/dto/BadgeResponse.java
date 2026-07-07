package com.singha.stackoverflow.springwebapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeResponse {

    private Long badgeId;

    @NotBlank(message = "Badge Name cannot be blank")
    private String badgeName;

    private String description;

    @NotBlank(message = "Badge type cannot be blank")
    @Pattern(
            regexp = "bronze|silver|gold",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Badge type must be bronze, silver, or gold"
    )
    private String badgeType;
}