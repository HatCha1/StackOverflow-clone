package com.singha.stackoverflow.springwebapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TagResponseDTO {
    private Long tagId;
    private String tagName;
    private String description;
    private long questionCount;
    private long askedTodayCount;
}