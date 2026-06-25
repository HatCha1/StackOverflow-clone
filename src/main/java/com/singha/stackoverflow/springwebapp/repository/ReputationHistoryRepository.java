package com.singha.stackoverflow.springwebapp.repository;

import com.singha.stackoverflow.springwebapp.entity.ReputationHistory;
import com.singha.stackoverflow.springwebapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReputationHistoryRepository extends JpaRepository<ReputationHistory, Long> {
    List<ReputationHistory> findByUser(User user);

    List<ReputationHistory> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
    /*
     SELECT *
        FROM reputation_history
        WHERE user_id = ?
        ORDER BY created_at DESC;
    */
}
