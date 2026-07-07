package com.singha.stackoverflow.springwebapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuestionCommentCreateDTO {
    private Long userId;

    private Long questionId;

    @NotBlank(message = "Body cannot be blank")
    private String body;
}