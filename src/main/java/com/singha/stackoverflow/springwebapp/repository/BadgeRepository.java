package com.singha.stackoverflow.springwebapp.repository;

import com.singha.stackoverflow.springwebapp.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    List<Badge> findByBadgeType(String badgeType);

    /*
        SELECT *
        FROM badges
        WHERE badge_type = ?;
    */

    List<Badge> findByBadgeNameContainingIgnoreCase(String keyword);

    /*
        SELECT *
        FROM badges
        WHERE LOWER(badge_name) LIKE LOWER('%keyword%');
    */

    boolean existsByBadgeName(String badgeName);

    /*
        SELECT COUNT(*)
        FROM badges
        WHERE badge_name = ?;
    */
}