package com.singha.stackoverflow.springwebapp.dto;

import lombok.Data;

@Data
public class QuestionCreateDTO {
    private String title;
    private String body;
    private Long userId; // The ID of the user asking the question
}