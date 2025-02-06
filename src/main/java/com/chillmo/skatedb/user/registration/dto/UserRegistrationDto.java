package com.chillmo.skatedb.user.registration.dto;


import com.chillmo.skatedb.entity.Difficulty;
import com.chillmo.skatedb.entity.ExperienceLevel;
import lombok.Data;

@Data
public class UserRegistrationDto {
    private String username;
    private String email;
    private String password;
    private ExperienceLevel experienceLevel; // Nutzt dein bestehendes Difficulty-Enum
}