package com.singha.stackoverflow.springwebapp.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String username;
    private String email;
    private String password;
}