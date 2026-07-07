package com.singha.stackoverflow.springwebapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDeleteConfirmDTO {

    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail; // Must match the authenticated user

    @NotBlank(message = "Confirmation phrase is required")
    private String confirmationPhrase; // Must equal exactly: "delete my account"
}
