package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.dto.AwardBadgeRequest;
import com.singha.stackoverflow.springwebapp.dto.UserBadgesResponse;
import com.singha.stackoverflow.springwebapp.entity.Badge;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.entity.UserBadges;
import com.singha.stackoverflow.springwebapp.entity.UserBadgesId;
import com.singha.stackoverflow.springwebapp.exception.DuplicateResourceException;
import com.singha.stackoverflow.springwebapp.exception.ResourceNotFoundException;
import com.singha.stackoverflow.springwebapp.repository.BadgeRepository;
import com.singha.stackoverflow.springwebapp.repository.UserBadgesRepository;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class UserBadgesService {
    @Autowired
    private UserBadgesRepository userBadgesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    public List<UserBadgesResponse> getBadgesByUserId() {

        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                        .getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<UserBadges> userBadges =
                userBadgesRepository.findByUser_UserIdOrderByAwardedAtDesc(currentUser.getUserId());

        return userBadges.stream()
                .map(this::convertToResponse)
                .toList();
    }

    public UserBadgesResponse awardBadge(AwardBadgeRequest request) {
        Long userId = request.getUserId();
        Long badgeId = request.getBadgeId();

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );

        Badge badge = badgeRepository.findById(badgeId).orElseThrow(
                () -> new ResourceNotFoundException("Badge not found")
        );

        boolean alreadyHasBadge =
                userBadgesRepository.existsByUser_UserIdAndBadge_BadgeId(userId, badgeId);

        if (alreadyHasBadge) {
            throw new DuplicateResourceException("User already has this badge");
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
