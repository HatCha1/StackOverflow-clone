package com.singha.stackoverflow.springwebapp.dto;

import lombok.Data;

@Data
public class AnswerCommentCreateDTO {
    private Long userId;
    private Long answerId;
    private String body;
}