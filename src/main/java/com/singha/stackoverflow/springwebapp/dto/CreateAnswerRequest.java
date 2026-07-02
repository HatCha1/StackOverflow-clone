package com.singha.stackoverflow.springwebapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnswerRequest {
    @NotBlank(message = "Body cannot be blank")
    private String body;
}