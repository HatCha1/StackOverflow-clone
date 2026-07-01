package com.singha.stackoverflow.springwebapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnswerCommentCreateDTO {
    private Long userId;

    private Long answerId;

    @NotBlank(message = "Body cannot be blank")
    private String body;
}