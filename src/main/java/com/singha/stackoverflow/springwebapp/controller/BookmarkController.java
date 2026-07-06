package com.singha.stackoverflow.springwebapp.controller;

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
            @PathVariable Long questionId) {
        return ResponseEntity.ok(bookmarkService.addBookmark(questionId));
    }

    @GetMapping("/users/bookmarks")
    public ResponseEntity<List<BookmarkResponseDTO>> getBookmarksByUserId() {
        return ResponseEntity.ok(bookmarkService.getBookmarksByUserId());
    }

    @DeleteMapping("/questions/{questionId}/bookmark")
    public ResponseEntity<String> removeBookmark(
            @PathVariable Long questionId) {
        bookmarkService.removeBookmark(questionId);
        return ResponseEntity.ok("Bookmark removed successfully");
    }
}