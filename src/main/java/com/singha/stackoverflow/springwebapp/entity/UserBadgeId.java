package com.singha.stackoverflow.springwebapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBadgeId {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "badge_id")
    private Long badgeId;
}