package com.singha.stackoverflow.springwebapp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnswerRequest {

    private Long userId;
    private String body;
}