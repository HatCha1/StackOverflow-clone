package com.singha.stackoverflow.springwebapp.repository;

import com.singha.stackoverflow.springwebapp.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByTagName(String tagName);

    boolean existsByTagName(String tagName);

    List<Tag> findByTagNameContainingIgnoreCase(String keyword);
}