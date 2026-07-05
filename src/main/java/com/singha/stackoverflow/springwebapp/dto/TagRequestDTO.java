package com.singha.stackoverflow.springwebapp.dto;

import lombok.Data;

@Data
public class TagRequestDTO {
    private String tagName;
    private String description;
}