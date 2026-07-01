package com.singha.stackoverflow.springwebapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentUpdateDTO {
    private Long userId; // The person trying to make the edit

    @NotBlank(message = "Body cannot be blank")
    private String body; // The new text content
}