package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.dto.AwardBadgeRequest;
import com.singha.stackoverflow.springwebapp.dto.UserBadgesResponse;
import com.singha.stackoverflow.springwebapp.entity.Badge;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.entity.UserBadges;
import com.singha.stackoverflow.springwebapp.entity.UserBadgesId;
import com.singha.stackoverflow.springwebapp.repository.BadgeRepository;
import com.singha.stackoverflow.springwebapp.repository.UserBadgesRepository;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserBadgesService {
    private UserBadgesRepository userBadgesRepository;
    private UserRepository userRepository;
    private BadgeRepository badgeRepository;

    public List<UserBadgesResponse> getBadgesByUserId(Long userId) {
        List<UserBadges> userBadges =
                userBadgesRepository.findByUser_UserIdOrderByAwardedAtDesc(userId);

        return userBadges.stream()
                .map(this::convertToResponse)
                .toList();
    }

    public UserBadgesResponse awardBadge(AwardBadgeRequest request) {
        Long userId = request.getUserId();
        Long badgeId = request.getBadgeId();

        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        Badge badge = badgeRepository.findById(badgeId).orElseThrow(
                () -> new RuntimeException("Badge not found")
        );

        boolean alreadyHasBadge =
                userBadgesRepository.existsByUser_UserIdAndBadge_BadgeId(userId, badgeId);

        if (alreadyHasBadge) {
            throw new RuntimeException("User already has this badge");
        }

        UserBadgesId id = new UserBadgesId(userId, badgeId);

        UserBadges userBadges = UserBadges.builder()
                .id(id)
                .user(user)
                .badge(badge)
                .awardedAt(LocalDateTime.now())
                .build();

        UserBadges savedUserBadge = userBadgesRepository.save(userBadges);

        return convertToResponse(savedUserBadge);
    }

    private UserBadgesResponse convertToResponse(UserBadges userBadge) {
        return UserBadgesResponse.builder()
                .userId(userBadge.getUser().getUserId())
                .username(userBadge.getUser().getUsername())

                .badgeId(userBadge.getBadge().getBadgeId())
                .badgeName(userBadge.getBadge().getBadgeName())
                .badgeType(userBadge.getBadge().getBadgeType())
                .description(userBadge.getBadge().getDescription())

                .awardedAt(userBadge.getAwardedAt())
                .build();
    }
}
