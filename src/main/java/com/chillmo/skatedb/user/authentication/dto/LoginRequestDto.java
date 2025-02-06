package com.chillmo.skatedb.user.authentication.dto;


import lombok.Data;

@Data
public class LoginRequestDto {
    private String username;
    private String password;
}