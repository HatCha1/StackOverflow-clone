package com.singha.stackoverflow.springwebapp.controller;

import com.singha.stackoverflow.springwebapp.dto.UserRegisterDTO;
import com.singha.stackoverflow.springwebapp.dto.UserResponseDTO;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRegisterDTO registerDTO) {
        // 1. Convert DTO -> Entity
        User userEntity = new User();
        userEntity.setUsername(registerDTO.getUsername());
        userEntity.setEmail(registerDTO.getEmail());
        userEntity.setPasswordHash(registerDTO.getPassword());
        userEntity.setReputation(0);

        userEntity.setDisplayName(registerDTO.getUsername());

        // 2. Save using your service layer
        User savedUser = userService.createUser(userEntity);

        // 3. Convert saved Entity -> Response DTO
        UserResponseDTO response = convertToResponseDTO(savedUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(this::convertToResponseDTO) // Safely maps Entity to DTO if found
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Helper method to convert Entity to DTO cleanly
    private UserResponseDTO convertToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setBio(user.getBio());
        dto.setReputation(user.getReputation());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setDisplayName(user.getDisplayName());
        return dto;
    }
}