package com.singha.stackoverflow.springwebapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Matches "generated always as identity"
    @Column(name = "user_id")
    private Long userId; // Use standard Long for Postgres bigint

    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "bio")
    private String bio;

    @Column(name = "reputation", nullable = false)
    private Integer reputation; // Matches integer type

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // Matches timestamp without time zone

    @Column(name = "display_name", length = 100, nullable = false)
    private String displayName; // Added this missing field from your table structure
}