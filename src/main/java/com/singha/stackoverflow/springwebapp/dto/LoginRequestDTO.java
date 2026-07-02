package com.singha.stackoverflow.springwebapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}