package com.chillmo.skatedb.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @Column(nullable = false)
    @NotBlank
    @JsonIgnore // Verhindert Passwort-Exposition in JSON-Responses
    private String password;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(length = 500)
    @Size(max = 500)
    private String bio;

    @Column(length = 100)
    @Size(max = 100)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level")
    private ExperienceLevel experienceLevel;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Stand stand;

    /**
     * Flag, ob der User-Account aktiviert/bestätigt ist.
     */
    @Column(nullable = false)
    private boolean enabled;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (!this.enabled) {
            this.enabled = false; // Standardmäßig deaktiviert bis Bestätigung, falls nicht explizit gesetzt
        }
    }

    public Boolean getEnabled() {
        return enabled;
    }
}
