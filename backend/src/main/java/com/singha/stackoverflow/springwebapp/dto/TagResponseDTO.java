package com.singha.stackoverflow.springwebapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagResponseDTO {
    private Long tagId;
    private String tagName;
    private String description;
}