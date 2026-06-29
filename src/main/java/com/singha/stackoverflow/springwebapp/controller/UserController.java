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
        // 1. Convert Incoming DTO -> Database Entity
        User userEntity = new User();
        userEntity.setUsername(registerDTO.getUsername());
        userEntity.setEmail(registerDTO.getEmail());
        userEntity.setPasswordHash(registerDTO.getPassword()); // Pulls plain text input password
        userEntity.setReputation(0);
        userEntity.setDisplayName(registerDTO.getUsername());

        // 2. Save into PostgreSQL
        User savedUser = userService.createUser(userEntity);

        // 3. Convert saved Database Entity -> Clean Outgoing Response DTO
        UserResponseDTO response = convertToResponseDTO(savedUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(convertToResponseDTO(user));
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