package com.singha.stackoverflow.springwebapp.repository;

import com.singha.stackoverflow.springwebapp.entity.UserBadges;
import com.singha.stackoverflow.springwebapp.entity.UserBadgesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgesRepository extends JpaRepository<UserBadges, UserBadgesId> {
    List<UserBadges> findByUser_UserIdOrderByAwardedAtDesc(Long userId);
    /*
     SELECT *
        FROM user_badges
        WHERE user_id = ?
        ORDER BY awarded_at DESC;
    */
    boolean existsByUser_UserIdAndBadge_BadgeId(Long userId, Long badgeId);
}
