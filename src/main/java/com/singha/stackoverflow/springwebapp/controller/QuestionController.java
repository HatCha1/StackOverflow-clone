package com.singha.stackoverflow.springwebapp.controller;

import com.singha.stackoverflow.springwebapp.dto.QuestionCreateDTO;
import com.singha.stackoverflow.springwebapp.dto.QuestionResponseDTO;
import com.singha.stackoverflow.springwebapp.entity.Question;
import com.singha.stackoverflow.springwebapp.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        QuestionResponseDTO response = new QuestionResponseDTO();
        response.setQuestionId(savedQuestion.getQuestionId());
        response.setTitle(savedQuestion.getTitle());
        response.setBody(savedQuestion.getBody());
        response.setViewCount(savedQuestion.getViewCount());
        response.setIsClosed(savedQuestion.getIsClosed());
        response.setCreatedAt(savedQuestion.getCreatedAt());
        response.setAuthorName(savedQuestion.getUser().getUsername()); // Reached through relationship link

        return ResponseEntity.ok(response);
    }
}