package com.singha.stackoverflow.springwebapp.controller;

import com.singha.stackoverflow.springwebapp.dto.BookmarkRequestDTO;
import com.singha.stackoverflow.springwebapp.dto.BookmarkResponseDTO;
import com.singha.stackoverflow.springwebapp.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @PostMapping("/questions/{questionId}/bookmark")
    public ResponseEntity<BookmarkResponseDTO> addBookmark(
            @PathVariable Long questionId,
            @RequestBody BookmarkRequestDTO request) {
        return ResponseEntity.ok(bookmarkService.addBookmark(questionId, request));
    }

    @GetMapping("/users/{userId}/bookmarks")
    public ResponseEntity<List<BookmarkResponseDTO>> getBookmarksByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(bookmarkService.getBookmarksByUserId(userId));
    }

    @DeleteMapping("/questions/{questionId}/bookmark")
    public ResponseEntity<String> removeBookmark(
            @PathVariable Long questionId,
            @RequestParam Long userId) {
        bookmarkService.removeBookmark(questionId, userId);
        return ResponseEntity.ok("Bookmark removed successfully");
    }
}