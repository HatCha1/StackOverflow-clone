package com.singha.stackoverflow.springwebapp.controller;

import com.singha.stackoverflow.springwebapp.dto.TagRequestDTO;
import com.singha.stackoverflow.springwebapp.dto.TagResponseDTO;
import com.singha.stackoverflow.springwebapp.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @PostMapping
    public ResponseEntity<TagResponseDTO> createTag(@RequestBody TagRequestDTO request) {
        return ResponseEntity.ok(tagService.createTag(request));
    }

    @GetMapping
    public ResponseEntity<List<TagResponseDTO>> getAllTags(
        @RequestParam(defaultValue = "popular") String sort,
        @RequestParam(defaultValue = "") String search) {
            return ResponseEntity.ok(tagService.getAllTagsWithFilters(sort, search));
    }

    @GetMapping("/{tagId}")
    public ResponseEntity<TagResponseDTO> getTagById(@PathVariable Long tagId) {
        return ResponseEntity.ok(tagService.getTagById(tagId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TagResponseDTO>> searchTags(@RequestParam String keyword) {
        return ResponseEntity.ok(tagService.searchTags(keyword));
    }
}