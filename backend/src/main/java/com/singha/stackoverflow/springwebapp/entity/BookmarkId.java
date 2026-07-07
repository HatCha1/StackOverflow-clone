package com.singha.stackoverflow.springwebapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class BookmarkId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "question_id")
    private Long questionId;
}