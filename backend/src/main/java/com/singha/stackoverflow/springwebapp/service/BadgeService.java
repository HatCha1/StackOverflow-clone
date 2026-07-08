package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.badge.rule.BadgeRule;
import com.singha.stackoverflow.springwebapp.entity.Badge;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.entity.UserBadges;
import com.singha.stackoverflow.springwebapp.exception.DuplicateResourceException;
import com.singha.stackoverflow.springwebapp.exception.ResourceNotFoundException;
import com.singha.stackoverflow.springwebapp.repository.BadgeRepository;
import com.singha.stackoverflow.springwebapp.repository.UserBadgesRepository;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserBadgesRepository userBadgesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private List<BadgeRule> badgeRules;

    public Badge createBadge(Badge badge) {
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

    public void awardBadgeIfNotOwned(Badge badge) {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                        .getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean alreadyOwned = userBadgesRepository.existsByUser_UserIdAndBadge_BadgeId(
                currentUser.getUserId(),
                badge.getBadgeId()
        );

        if (alreadyOwned) {
            return;
        }

        UserBadges userBadge = new UserBadges();
        userBadge.setUser(currentUser);
        userBadge.setBadge(badge);
        userBadge.setAwardedAt(LocalDateTime.now());

        userBadgesRepository.save(userBadge);
    }

    @Transactional
    public void checkBadges() {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                        .getName();

        User currentUser = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        for (BadgeRule rule : badgeRules) {
            if (rule.isSatisfied(currentUser)) {
                Badge badge = badgeRepository.findByBadgeName(rule.getBadgeName())
                        .orElseThrow(() -> new ResourceNotFoundException("Badge not found: " + rule.getBadgeName()));

                awardBadgeIfNotOwned(badge);
            }
        }
    }
}