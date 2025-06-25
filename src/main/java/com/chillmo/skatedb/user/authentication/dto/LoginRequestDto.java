package com.chillmo.skatedb.user.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotBlank(message = "Username or email is required")
    private String identifier;

    @NotBlank(message = "Password is required")
    private String password;
}