package com.chillmo.skatedb.trick_user;

import com.chillmo.skatedb.trick.domain.Trick;
import com.chillmo.skatedb.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "trick_id", nullable = false)
    private Trick trick;

    @Column(name = "date_learned")
    private LocalDateTime dateLearned;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private TrickStatus status;

}