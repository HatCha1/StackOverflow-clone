package com.singha.stackoverflow.springwebapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "badges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_id", nullable = false)
    private Long badgeId;

    @Column(name = "badge_name", nullable = false, length = 100)
    private String badgeName;

    @Column(name = "description")
    private String description;

    @Column(name = "badge_type", length = 20, nullable = false)
    private String badgeType;
}