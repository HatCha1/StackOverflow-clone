package com.singha.stackoverflow.springwebapp.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Long userId;
    private String username;
    private String email;
    private String displayName;
    private String bio;
    private Integer reputation;
    private LocalDateTime createdAt;
}