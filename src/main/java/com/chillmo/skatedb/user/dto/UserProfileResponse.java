package com.chillmo.skatedb.user.dto;

import com.chillmo.skatedb.user.domain.ExperienceLevel;
import com.chillmo.skatedb.user.domain.Role;
import com.chillmo.skatedb.user.domain.Stand;
import com.chillmo.skatedb.user.domain.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

/**
 * Response payload representing the public parts of a user's profile.
 */
public record UserProfileResponse(
        Long id,
        String username,
        String email,
        String profilePictureUrl,
        String bio,
        String location,
        ExperienceLevel experienceLevel,
        Stand stand,
        Set<Role> roles,
        LocalDateTime createdAt,
        LocalDateTime lastLogin,
        boolean enabled
) {
    public static UserProfileResponse from(User user) {
        Set<Role> roles = user.getRoles() == null ? Collections.emptySet() : Set.copyOf(user.getRoles());
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfilePictureUrl(),
                user.getBio(),
                user.getLocation(),
                user.getExperienceLevel(),
                user.getStand(),
                roles,
                user.getCreatedAt(),
                user.getLastLogin(),
                Boolean.TRUE.equals(user.getEnabled())
        );
    }
}
