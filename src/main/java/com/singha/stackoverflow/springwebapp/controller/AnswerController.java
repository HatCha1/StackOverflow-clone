package com.singha.stackoverflow.springwebapp.controller;

import com.singha.stackoverflow.springwebapp.dto.AnswerResponse;
import com.singha.stackoverflow.springwebapp.dto.CreateAnswerRequest;
import com.singha.stackoverflow.springwebapp.dto.UpdateAnswerRequest;
import com.singha.stackoverflow.springwebapp.service.AnswerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @PostMapping("/api/questions/{questionId}/answers")
    public AnswerResponse createAnswer(@PathVariable Long questionId, @Valid @RequestBody CreateAnswerRequest request) {
        return answerService.createAnswer(
                questionId,
                request.getBody()
        );
    }

    @GetMapping("/api/questions/{questionId}/answers")
    public List<AnswerResponse> getAnswersByQuestionId(@PathVariable Long questionId) {
        return answerService.getAnswersByQuestionId(questionId);
    }

    @GetMapping("/api/users/{userId}/answers")
    public List<AnswerResponse> getAnswersByUserId(@PathVariable Long userId) {
        return answerService.getAnswersByUserId(userId);
    }

    @GetMapping("/api/answers/{answerId}")
    public AnswerResponse getAnswerById(@PathVariable Long answerId) {
        return answerService.getAnswerById(answerId);
    }

    @PutMapping("/api/answers/{answerId}")
    public AnswerResponse updateAnswer(@PathVariable Long answerId, @Valid @RequestBody UpdateAnswerRequest request) {
        return answerService.updateAnswer(answerId, request.getBody());
    }

    @DeleteMapping("/api/answers/{answerId}")
    public void deleteAnswer(@PathVariable Long answerId) {
        answerService.deleteAnswer(answerId);
    }

    @PutMapping("/api/answers/{answerId}/accept")
    public AnswerResponse acceptAnswer(@PathVariable Long answerId) {
        return answerService.acceptAnswer(answerId);
    }
}