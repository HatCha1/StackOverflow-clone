package com.singha.stackoverflow.springwebapp.dto;

import com.singha.stackoverflow.springwebapp.enums.VoteType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteRequestDTO {
    @NotNull(message = "Vote type can not be blank")
    private VoteType voteType;
}