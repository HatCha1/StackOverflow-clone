package com.singha.stackoverflow.springwebapp.controller;

import com.singha.stackoverflow.springwebapp.dto.VoteRequestDTO;
import com.singha.stackoverflow.springwebapp.dto.VoteResponseDTO;
import com.singha.stackoverflow.springwebapp.service.VoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PostMapping("/questions/{questionId}/vote")
    public ResponseEntity<VoteResponseDTO> voteQuestion(
            @PathVariable Long questionId,
            @Valid @RequestBody VoteRequestDTO request) {
        return ResponseEntity.ok(voteService.voteQuestion(questionId, request.getVoteType()));
    }

    @PostMapping("/answers/{answerId}/vote")
    public ResponseEntity<VoteResponseDTO> voteAnswer(
            @PathVariable Long answerId,
            @Valid @RequestBody VoteRequestDTO request) {
        return ResponseEntity.ok(voteService.voteAnswer(answerId, request.getVoteType()));
    }
}