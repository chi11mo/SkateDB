package com.chillmo.skatedb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 2000)
    private String description;

    // Verwendet dein vorhandenes Difficulty-Enum
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    // Neues Attribut: TrickType (Vert oder Street)
    @Enumerated(EnumType.STRING)
    @Column(name = "trick_type")
    private TrickType trickType;

    @Column(length = 100)
    private String category;

    private String imageUrl;
    private String videoUrl;

    // Selbstreferenzielle Many-to-Many Beziehung für Voraussetzungen
    @ManyToMany
    @JoinTable(
            name = "trick_prerequisites",
            joinColumns = @JoinColumn(name = "trick_id"),
            inverseJoinColumns = @JoinColumn(name = "prerequisite_id")
    )
    @Builder.Default
    private List<Trick> prerequisites = new ArrayList<>();
}