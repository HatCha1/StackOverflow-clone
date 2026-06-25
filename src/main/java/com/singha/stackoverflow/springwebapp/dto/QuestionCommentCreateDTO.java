package com.singha.stackoverflow.springwebapp.dto;

import lombok.Data;

@Data
public class QuestionCommentCreateDTO {
    private Long userId;
    private Long questionId;
    private String body;
}