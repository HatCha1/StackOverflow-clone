package com.singha.stackoverflow.springwebapp.controller;

import com.singha.stackoverflow.springwebapp.dto.QuestionCreateDTO;
import com.singha.stackoverflow.springwebapp.dto.QuestionResponseDTO;
import com.singha.stackoverflow.springwebapp.dto.QuestionUpdateDTO;
import com.singha.stackoverflow.springwebapp.entity.Question;
import com.singha.stackoverflow.springwebapp.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping
    public ResponseEntity<QuestionResponseDTO> createQuestion(@RequestBody QuestionCreateDTO createDTO) {
        // 1. Process the request via service layer
        Question savedQuestion = questionService.createQuestion(
                createDTO.getTitle(),
                createDTO.getBody(),
                createDTO.getUserId()
        );

        // 2. Map entity output data back to a clean response DTO structure
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
        return questionService.getQuestionById(id)
                .map(this::convertToResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestion(
            @PathVariable Long id,
            @RequestBody QuestionUpdateDTO updateDTO) {
        try {
            Question updatedQuestion = questionService.updateQuestion(
                    id,
                    updateDTO.getUserId(),
                    updateDTO.getTitle(),
                    updateDTO.getBody()
            );
            return ResponseEntity.ok(convertToResponseDTO(updatedQuestion));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 4. DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestion(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            questionService.deleteQuestion(id, userId);
            return ResponseEntity.ok("Question deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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