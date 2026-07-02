package com.singha.stackoverflow.springwebapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuestionUpdateDTO {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Body cannot be blank")
    private String body;
}