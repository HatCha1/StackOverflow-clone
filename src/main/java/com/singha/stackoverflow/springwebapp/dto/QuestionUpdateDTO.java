package com.singha.stackoverflow.springwebapp.dto;

import lombok.Data;

@Data
public class QuestionUpdateDTO {
    private Long userId; // The person trying to perform the edit
    private String title;
    private String body;
}