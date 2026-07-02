package com.singha.stackoverflow.springwebapp.controller;

import com.singha.stackoverflow.springwebapp.dto.QuestionCreateDTO;
import com.singha.stackoverflow.springwebapp.dto.QuestionResponseDTO;
import com.singha.stackoverflow.springwebapp.dto.QuestionUpdateDTO;
import com.singha.stackoverflow.springwebapp.entity.Question;
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
        Question savedQuestion = questionService.createQuestion(
                createDTO.getTitle(),
                createDTO.getBody(),
                username
        );

        QuestionResponseDTO response = convertToResponseDTO(savedQuestion);
        return ResponseEntity.ok(response);
    }

    // 1. READ ALL
    @GetMapping
    public ResponseEntity<List<QuestionResponseDTO>> getAllQuestions() {
        List<Question> questions = questionService.getAllQuestions();
        List<QuestionResponseDTO> response = questions.stream()
                .map(this::convertToResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    // 2. READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDTO> getQuestionById(@PathVariable Long id) {
        // getQuestionById() throws ResourceNotFoundException if not found — no null check needed
        Question question = questionService.getQuestionById(id);
        return ResponseEntity.ok(convertToResponseDTO(question));
    }

    // 3. UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionUpdateDTO updateDTO,
            Authentication authentication) {
        
        String username = authentication.getName();
        Question updatedQuestion = questionService.updateQuestion(
                id,
                username,
                updateDTO.getTitle(),
                updateDTO.getBody()
        );
        return ResponseEntity.ok(convertToResponseDTO(updatedQuestion));
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

    // conversion mapping helper method
    private QuestionResponseDTO convertToResponseDTO(Question savedQuestion) {
        QuestionResponseDTO response = new QuestionResponseDTO();
        response.setQuestionId(savedQuestion.getQuestionId());
        response.setTitle(savedQuestion.getTitle());
        response.setBody(savedQuestion.getBody());
        response.setViewCount(savedQuestion.getViewCount());
        response.setIsClosed(savedQuestion.getIsClosed());
        response.setCreatedAt(savedQuestion.getCreatedAt());
        response.setAuthorName(savedQuestion.getUser().getUsername());
        return response;
    }

}