package com.singha.stackoverflow.springwebapp.controller;

import com.singha.stackoverflow.springwebapp.dto.ReputationHistoryResponse;
import com.singha.stackoverflow.springwebapp.service.ReputationHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/reputation-history")
public class ReputationHistoryController {
    @Autowired
    private ReputationHistoryService reputationHistoryService;

    @GetMapping
    public List<ReputationHistoryResponse> getReputationHistoryByUserId() {
        return reputationHistoryService.getReputationHistoryByUserId();
    }
}