package com.singha.stackoverflow.springwebapp.repository;

import com.singha.stackoverflow.springwebapp.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    // Spring Data JPA automatically gives you: save(), findById(), deleteById()

    // Custom query method: Finds all questions posted by a specific user ID
    List<Question> findByUser_UserId(Long userId);
}