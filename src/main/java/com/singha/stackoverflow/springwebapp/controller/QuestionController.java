package com.singha.stackoverflow.springwebapp.controller;

import com.singha.stackoverflow.springwebapp.dto.QuestionCreateDTO;
import com.singha.stackoverflow.springwebapp.dto.QuestionResponseDTO;
import com.singha.stackoverflow.springwebapp.dto.QuestionUpdateDTO;
import com.singha.stackoverflow.springwebapp.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping
    public ResponseEntity<QuestionResponseDTO> createQuestion(
            @Valid @RequestBody QuestionCreateDTO createDTO,
            Authentication authentication) {

        String username = authentication.getName();
        return ResponseEntity.ok(questionService.createQuestion(
                createDTO.getTitle(),
                createDTO.getBody(),
                username));
    }

    // 1. READ ALL
    @GetMapping
    public ResponseEntity<List<QuestionResponseDTO>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    // 2. READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDTO> getQuestionById(@PathVariable Long id,
                                                               Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(questionService.getQuestionById(id, username));
    }

    // 3. UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponseDTO> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionUpdateDTO updateDTO,
            Authentication authentication) {

        String username = authentication.getName();
        return ResponseEntity.ok(questionService.updateQuestion(
                id,
                username,
                updateDTO.getTitle(),
                updateDTO.getBody()));
    }

    // 4. DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestion(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();
        questionService.deleteQuestion(id, username);
        return ResponseEntity.ok("Question deleted successfully.");
    }
}