package com.singha.stackoverflow.springwebapp.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String username;
    private String password;
}