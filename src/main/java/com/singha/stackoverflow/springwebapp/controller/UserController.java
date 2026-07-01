package com.singha.stackoverflow.springwebapp.controller;

import com.singha.stackoverflow.springwebapp.dto.LoginRequestDTO;
import com.singha.stackoverflow.springwebapp.dto.LoginResponseDTO;
import com.singha.stackoverflow.springwebapp.dto.UserRegisterDTO;
import com.singha.stackoverflow.springwebapp.dto.UserResponseDTO;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import com.singha.stackoverflow.springwebapp.security.JwtUtils;
import com.singha.stackoverflow.springwebapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRegisterDTO registerDTO) {
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginDTO) {
        // 1. Find the user by username
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // 2. Compare incoming raw password with the hashed password in PostgreSQL
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }

        // 3. If password matches, generate the cryptographically signed JWT
        String token = jwtUtils.generateToken(user.getUsername());

        // 4. Return the token to React/Postman
        return ResponseEntity.ok(new LoginResponseDTO(token, user.getUsername()));
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