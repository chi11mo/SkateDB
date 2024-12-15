package com.chillmo.skatedb.dto;

import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequest {
    private String usernameOrEmail;
    private String password;
}