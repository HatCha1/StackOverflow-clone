package com.singha.stackoverflow.springwebapp.controller;

import com.singha.stackoverflow.springwebapp.dto.QuestionCreateDTO;
import com.singha.stackoverflow.springwebapp.dto.QuestionResponseDTO;
import com.singha.stackoverflow.springwebapp.dto.QuestionUpdateDTO;
import com.singha.stackoverflow.springwebapp.entity.Question;
import com.singha.stackoverflow.springwebapp.service.QuestionService;
import jakarta.validation.Valid;
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
    public ResponseEntity<QuestionResponseDTO> createQuestion(@Valid @RequestBody QuestionCreateDTO createDTO) {
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
        Question question = questionService.getQuestionById(id);

        if (question == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(convertToResponseDTO(question));
    }

    // 3. UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionUpdateDTO updateDTO) {
        Question updatedQuestion = questionService.updateQuestion(
                id,
                updateDTO.getUserId(),
                updateDTO.getTitle(),
                updateDTO.getBody()
        );
        return ResponseEntity.ok(convertToResponseDTO(updatedQuestion));
    }

    // 4. DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestion(
            @PathVariable Long id,
            @RequestParam Long userId) {

        questionService.deleteQuestion(id, userId);
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