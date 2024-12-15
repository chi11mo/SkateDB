package com.chillmo.skatedb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "user_tricks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTrick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trick_id", nullable = false)
    private AvailableTrick trick;

    @Column(name = "learned_at", updatable = false)
    private LocalDateTime learnedAt;

    @PrePersist
    protected void onPersist() {
        this.learnedAt = LocalDateTime.now();
    }
}