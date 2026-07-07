package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.dto.ChangePasswordDTO;
import com.singha.stackoverflow.springwebapp.dto.UserDeleteConfirmDTO;
import com.singha.stackoverflow.springwebapp.dto.UserResponseDTO;
import com.singha.stackoverflow.springwebapp.dto.UserUpdateDTO;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.exception.BadRequestException;
import com.singha.stackoverflow.springwebapp.exception.DuplicateResourceException;
import com.singha.stackoverflow.springwebapp.exception.ResourceNotFoundException;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("Email already exists!");
        }

        String hashedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);
        user.setCreatedAt(java.time.LocalDateTime.now());
        return toDTO(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // UPDATE — patch: only apply fields that were provided in the DTO
    @Transactional
    public UserResponseDTO updateUser(String username, UserUpdateDTO updateDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (updateDTO.getDisplayName() != null && !updateDTO.getDisplayName().isBlank()) {
            user.setDisplayName(updateDTO.getDisplayName());
        }

        if (updateDTO.getBio() != null) {
            // Allow blank string to clear bio (fix for issue #10)
            user.setBio(updateDTO.getBio().isBlank() ? null : updateDTO.getBio());
        }

        return toDTO(userRepository.save(user));
    }

    // CHANGE PASSWORD — verifies current password before applying the new one
    @Transactional
    public void changePassword(String username, ChangePasswordDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        // 1. Verify the current password is correct
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect.");
        }

        // 2. Hash and save the new password
        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    // DELETE — requires the user to confirm their identity and type the phrase exactly
    @Transactional
    public void deleteUser(String username, UserDeleteConfirmDTO confirmDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        // 1. Verify they entered their own username or email
        boolean identityMatches = user.getUsername().equals(confirmDTO.getUsernameOrEmail())
                || user.getEmail().equals(confirmDTO.getUsernameOrEmail());

        if (!identityMatches) {
            throw new BadRequestException("Username or email does not match your account.");
        }

        // 2. Verify they typed the exact confirmation phrase
        if (!"delete my account".equals(confirmDTO.getConfirmationPhrase())) {
            throw new BadRequestException("Please type \"delete my account\" exactly to confirm.");
        }

        userRepository.delete(user);
    }

    // Private mapping helper — single source of truth for User → DTO conversion
    public UserResponseDTO toDTO(User user) {
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