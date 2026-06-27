package com.singha.stackoverflow.springwebapp.repository;

import com.singha.stackoverflow.springwebapp.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestion_QuestionIdOrderByCreatedAtAsc(Long questionId);

    /*
        SELECT *
        FROM answers
        WHERE question_id = ?
        ORDER BY created_at ASC;
    */

    List<Answer> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    /*
        SELECT *
        FROM answers
        WHERE user_id = ?
        ORDER BY created_at DESC;
    */

    List<Answer> findByQuestion_QuestionIdAndIsAcceptedTrue(Long questionId);

    /*
        SELECT *
        FROM answers
        WHERE question_id = ?
        AND is_accepted = true;
    */
}