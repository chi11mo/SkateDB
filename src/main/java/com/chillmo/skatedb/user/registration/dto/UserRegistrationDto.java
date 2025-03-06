package com.chillmo.skatedb.user.registration.dto;


import com.chillmo.skatedb.user.domain.ExperienceLevel;
import com.chillmo.skatedb.user.domain.Stand;
import lombok.Data;

@Data
public class UserRegistrationDto {
    private String username;
    private String email;
    private String password;
    private ExperienceLevel experienceLevel;
    private Stand stand;
}