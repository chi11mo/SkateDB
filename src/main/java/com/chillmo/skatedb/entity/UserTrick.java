package com.chillmo.skatedb.entity;

import com.chillmo.skatedb.user.domain.User;
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

    // Verweis auf den User, der den Trick erlernt hat
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Verweis auf den Trick
    @ManyToOne(optional = false)
    @JoinColumn(name = "trick_id", nullable = false)
    private Trick trick;

    // Datum, an dem der Trick erlernt wurde
    @Column(name = "date_learned")
    private LocalDateTime dateLearned;

    // Optional: Status oder Bewertung des Tricks (z. B. "in progress", "mastered")
    @Column(name = "status", length = 50)
    private String status;
}