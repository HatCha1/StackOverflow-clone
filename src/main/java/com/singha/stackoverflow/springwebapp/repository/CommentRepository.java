package com.singha.stackoverflow.springwebapp.repository;

import com.singha.stackoverflow.springwebapp.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 1. Fetch all comments left on a specific question
    // Maps to: WHERE question_id = ? ORDER BY created_at ASC
    List<Comment> findByQuestion_QuestionIdOrderByCreatedAtAsc(Long questionId);

    // 2. Fetch all comments left on a specific answer
    // Maps to: WHERE answer_id = ? ORDER BY created_at ASC
    List<Comment> findByAnswer_AnswerIdOrderByCreatedAtAsc(Long answerId);

    // 3. Fetch all comments written by a specific user (e.g., for a user profile activity history log)
    List<Comment> findByUser_UserId(Long userId);
}