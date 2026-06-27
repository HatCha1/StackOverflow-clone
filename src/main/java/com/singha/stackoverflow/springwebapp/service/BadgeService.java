package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.entity.Badge;
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
        if (badgeRepository.existsByBadgeName(badge.getBadgeName())) {
            throw new RuntimeException("Badge name already exists");
        }

        return badgeRepository.save(badge);
    }

    public Optional<Badge> getBadgeById(Long badgeId) {
        return badgeRepository.findById(badgeId);
    }

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    public List<Badge> getBadgesByType(String badgeType) {
        return badgeRepository.findByBadgeType(badgeType);
    }

    public List<Badge> searchBadgesByName(String keyword) {
        return badgeRepository.findByBadgeNameContainingIgnoreCase(keyword);
    }
}