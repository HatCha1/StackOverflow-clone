package com.singha.stackoverflow.springwebapp.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {
    // All fields are optional — only non-null values will be applied

    @Size(max = 100, message = "Display name must be 100 characters or less")
    private String displayName;

    @Size(max = 500, message = "Bio must be 500 characters or less")
    private String bio;
}
