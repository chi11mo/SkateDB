package com.chillmo.skatedb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tricks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    // Many tricks can belong to one user.
    // We use LAZY loading to avoid fetching user data unnecessarily.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "learned_at", updatable = false)
    private LocalDateTime learnedAt;

    @PrePersist
    protected void onPersist() {
        this.learnedAt = LocalDateTime.now();
    }
}