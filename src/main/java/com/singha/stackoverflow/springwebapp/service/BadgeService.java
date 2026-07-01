package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.entity.Badge;
import com.singha.stackoverflow.springwebapp.exception.BadRequestException;
import com.singha.stackoverflow.springwebapp.exception.DuplicateResourceException;
import com.singha.stackoverflow.springwebapp.exception.ResourceNotFoundException;
import com.singha.stackoverflow.springwebapp.repository.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    public Badge createBadge(Badge badge) {
        if (badge.getBadgeName() == null || badge.getBadgeName().isBlank()) {
            throw new BadRequestException("Badge name cannot be empty");
        }
        if (!badge.getBadgeType().equalsIgnoreCase("bronze")
                && !badge.getBadgeType().equalsIgnoreCase("silver")
                && !badge.getBadgeType().equalsIgnoreCase("gold")) {
            throw new BadRequestException("Badge type must be bronze, silver, or gold");
        }
        if (badgeRepository.existsByBadgeName(badge.getBadgeName())) {
            throw new DuplicateResourceException("Badge name already exists");
        }

        return badgeRepository.save(badge);
    }

    public Badge getBadgeById(Long badgeId) {
        return badgeRepository.findById(badgeId).orElseThrow(
                () -> new ResourceNotFoundException("Badge not found")
        );
    }

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    public List<Badge> getBadgesByType(String badgeType) { return badgeRepository.findByBadgeType(badgeType); }

    public List<Badge> searchBadgesByName(String keyword) {
        return badgeRepository.findByBadgeNameContainingIgnoreCase(keyword);
    }
}