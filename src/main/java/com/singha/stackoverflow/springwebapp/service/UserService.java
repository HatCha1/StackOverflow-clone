package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.dto.ChangePasswordDTO;
import com.singha.stackoverflow.springwebapp.dto.UserDeleteConfirmDTO;
import com.singha.stackoverflow.springwebapp.dto.UserUpdateDTO;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.exception.BadRequestException;
import com.singha.stackoverflow.springwebapp.exception.DuplicateResourceException;
import com.singha.stackoverflow.springwebapp.exception.ResourceNotFoundException;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("Email already exists!");
        }

        String hashedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);
        user.setCreatedAt(java.time.LocalDateTime.now());
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );
    }

    // UPDATE — patch: only apply fields that were provided in the DTO
    public User updateUser(String username, UserUpdateDTO updateDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (updateDTO.getDisplayName() != null && !updateDTO.getDisplayName().isBlank()) {
            user.setDisplayName(updateDTO.getDisplayName());
        }

        if (updateDTO.getBio() != null && !updateDTO.getBio().isBlank()) {
            user.setBio(updateDTO.getBio());
        }

        return userRepository.save(user);
    }

    // CHANGE PASSWORD — verifies current password before applying the new one
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
}