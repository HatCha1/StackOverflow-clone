package com.singha.stackoverflow.springwebapp.dto;

import com.singha.stackoverflow.springwebapp.enums.VoteType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteResponseDTO {
    private Long voteId;
    private Long userId;
    private Long questionId;
    private Long answerId;
    private VoteType voteType;
    private Long voteScore;
}