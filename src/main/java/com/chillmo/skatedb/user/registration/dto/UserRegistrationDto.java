package com.chillmo.skatedb.user.registration.dto;


import com.chillmo.skatedb.user.domain.ExperienceLevel;
import lombok.Data;

@Data
public class UserRegistrationDto {
    private String username;
    private String email;
    private String password;
    private ExperienceLevel experienceLevel; // Nutzt dein bestehendes Difficulty-Enum
}