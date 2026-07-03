package com.singha.stackoverflow.springwebapp.dto;

import lombok.Data;

@Data
public class VoteRequestDTO {
    private Long userId;
    private String voteType;
}