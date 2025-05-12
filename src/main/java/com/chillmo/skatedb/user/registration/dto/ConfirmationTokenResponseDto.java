package com.chillmo.skatedb.user.registration.dto;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConfirmationTokenResponseDto {
    private String token;
    private LocalDateTime expiresAt;
}
