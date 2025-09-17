package com.chillmo.skatedb.user.email.dto;

import lombok.Data;

@Data
public class EmailConfirmationRequestDto {
    private String email;
    private String token;
}
