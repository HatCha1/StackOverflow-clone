package com.singha.stackoverflow.springwebapp.controller;

import com.singha.stackoverflow.springwebapp.dto.AwardBadgeRequest;
import com.singha.stackoverflow.springwebapp.dto.UserBadgesResponse;
import com.singha.stackoverflow.springwebapp.service.UserBadgesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class UserBadgesController {
    @Autowired
    private UserBadgesService userBadgesService;

    @GetMapping("/api/users/badges")
    public List<UserBadgesResponse> getUserBadgesByUserId() {
        return userBadgesService.getBadgesByUserId();
    }

    @PostMapping("/api/badges/award")
    public ResponseEntity<UserBadgesResponse> awardBadge(@Valid @RequestBody AwardBadgeRequest request) {
        UserBadgesResponse awardedBadge = userBadgesService.awardBadge(request);
        return ResponseEntity.ok(awardedBadge);
    }
}