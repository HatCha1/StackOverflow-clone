package com.singha.stackoverflow.springwebapp.repository;

import com.singha.stackoverflow.springwebapp.entity.Bookmark;
import com.singha.stackoverflow.springwebapp.entity.BookmarkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, BookmarkId> {

    List<Bookmark> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    boolean existsById_UserIdAndId_QuestionId(Long userId, Long questionId);
}