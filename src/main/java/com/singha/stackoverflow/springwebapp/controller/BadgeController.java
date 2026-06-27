package com.singha.stackoverflow.springwebapp.controller;

import com.singha.stackoverflow.springwebapp.dto.BadgeResponse;
import com.singha.stackoverflow.springwebapp.entity.Badge;
import com.singha.stackoverflow.springwebapp.service.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BadgeController {

    @Autowired
    private BadgeService badgeService;

    @PostMapping("/api/badges")
    public BadgeResponse createBadge(@RequestBody Badge badge) {
        Badge savedBadge = badgeService.createBadge(badge);
        return convertToResponse(savedBadge);
    }

    @GetMapping("/api/badges")
    public List<BadgeResponse> getAllBadges() {
        return badgeService.getAllBadges()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @GetMapping("/api/badges/{badgeId}")
    public BadgeResponse getBadgeById(@PathVariable Long badgeId) {
        Badge badge = badgeService.getBadgeById(badgeId).orElseThrow(
                () -> new RuntimeException("Badge not found")
        );

        return convertToResponse(badge);
    }

    @GetMapping("/api/badges/type/{badgeType}")
    public List<BadgeResponse> getBadgesByType(@PathVariable String badgeType) {
        return badgeService.getBadgesByType(badgeType)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @GetMapping("/api/badges/search")
    public List<BadgeResponse> searchBadgesByName(@RequestParam String keyword) {
        return badgeService.searchBadgesByName(keyword)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private BadgeResponse convertToResponse(Badge badge) {
        return BadgeResponse.builder()
                .badgeId(badge.getBadgeId())
                .badgeName(badge.getBadgeName())
                .description(badge.getDescription())
                .badgeType(badge.getBadgeType())
                .build();
    }
}