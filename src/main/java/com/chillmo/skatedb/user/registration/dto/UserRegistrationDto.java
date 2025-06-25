package com.chillmo.skatedb.user.registration.dto;

import com.chillmo.skatedb.user.domain.ExperienceLevel;
import com.chillmo.skatedb.user.domain.Stand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscores and hyphens")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String password;

    // Optional - will be set during onboarding
    private ExperienceLevel experienceLevel;

    // Optional - will be set during onboarding
    private Stand stand;
}