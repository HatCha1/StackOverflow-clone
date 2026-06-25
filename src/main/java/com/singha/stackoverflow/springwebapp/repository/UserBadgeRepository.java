package com.singha.stackoverflow.springwebapp.repository;

import com.singha.stackoverflow.springwebapp.entity.ReputationHistory;
import com.singha.stackoverflow.springwebapp.entity.UserBadge;
import com.singha.stackoverflow.springwebapp.entity.UserBadgeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<ReputationHistory, UserBadgeId> {
    List<UserBadge> findByUser(User user);

    List<UserBadge> findByUser_UserIdOrderByAwardedAtDesc(Long userId);
    /*
     SELECT *
        FROM user_badges
        WHERE user_id = ?
        ORDER BY awarded_at DESC;
    */
}
