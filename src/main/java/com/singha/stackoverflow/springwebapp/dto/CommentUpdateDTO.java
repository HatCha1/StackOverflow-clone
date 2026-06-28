package com.singha.stackoverflow.springwebapp.dto;

import lombok.Data;

@Data
public class CommentUpdateDTO {
    private Long userId; // The person trying to make the edit
    private String body; // The new text content
}