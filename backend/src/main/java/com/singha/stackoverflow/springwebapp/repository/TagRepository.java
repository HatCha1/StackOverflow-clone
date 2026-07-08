package com.singha.stackoverflow.springwebapp.repository;

import com.singha.stackoverflow.springwebapp.dto.TagResponseDTO;
import com.singha.stackoverflow.springwebapp.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByTagNameIgnoreCase(String name);

    boolean existsByTagName(String tagName);
    List<Tag> findByTagNameContainingIgnoreCase(String keyword);
    // Filter query projecting data straight into our Response DTO
    @Query("SELECT new com.singha.stackoverflow.springwebapp.dto.TagResponseDTO(" +
            "t.tagId, t.tagName, t.description, " +
            "COUNT(q.questionId), " +
            "SUM(CASE WHEN q.createdAt >= CURRENT_DATE THEN 1 ELSE 0 END)) " +
            "FROM Tag t " +
            "LEFT JOIN t.questions q " +
            "WHERE (:search = '' OR LOWER(t.tagName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "GROUP BY t.tagId, t.tagName, t.description " +
            "ORDER BY " +
            "CASE WHEN :sort = 'name' THEN t.tagName END ASC, " +
            "CASE WHEN :sort = 'popular' THEN COUNT(q.questionId) END DESC, " +
            "CASE WHEN :sort = 'new' THEN t.tagId END DESC")
    List<TagResponseDTO> findAllWithStatistics(@Param("sort") String sort, @Param("search") String search);
}