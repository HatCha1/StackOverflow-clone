package com.singha.stackoverflow.springwebapp.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private String username;

    public LoginResponseDTO(String token, String username) {
        this.token = token;
        this.username = username;
    }
}