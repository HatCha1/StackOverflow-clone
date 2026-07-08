package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.dto.TagRequestDTO;
import com.singha.stackoverflow.springwebapp.dto.TagResponseDTO;
import com.singha.stackoverflow.springwebapp.entity.Tag;
import com.singha.stackoverflow.springwebapp.exception.DuplicateResourceException;
import com.singha.stackoverflow.springwebapp.exception.ResourceNotFoundException;
import com.singha.stackoverflow.springwebapp.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public TagResponseDTO createTag(TagRequestDTO request) {
        if (tagRepository.existsByTagName(request.getTagName())) {
            throw new DuplicateResourceException("Tag already exists");
        }

        Tag tag = Tag.builder()
                .tagName(request.getTagName().toLowerCase().trim())
                .description(request.getDescription())
                .build();

        return convertToResponse(tagRepository.save(tag));
    }

    public List<TagResponseDTO> getAllTags() {
        return tagRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    public TagResponseDTO getTagById(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
        return convertToResponse(tag);
    }

    public List<TagResponseDTO> searchTags(String keyword) {
        return tagRepository.findByTagNameContainingIgnoreCase(keyword).stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<TagResponseDTO> getAllTagsWithFilters(String sort, String search) {
        String cleanSort = (sort == null) ? "popular" : sort.toLowerCase().trim();
        String cleanSearch = (search == null) ? "" : search.trim();

        return tagRepository.findAllWithStatistics(cleanSort, cleanSearch);
    }

    private TagResponseDTO convertToResponse(Tag tag) {
        return TagResponseDTO.builder()
                .tagId(tag.getTagId())
                .tagName(tag.getTagName())
                .description(tag.getDescription())
                .build();
    }
}