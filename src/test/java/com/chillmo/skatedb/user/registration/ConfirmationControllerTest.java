package com.chillmo.skatedb.user.registration;



import com.chillmo.skatedb.security.JwtAuthenticationFilter;

import com.chillmo.skatedb.user.registration.controller.ConfirmationController;
import com.chillmo.skatedb.user.registration.dto.ConfirmationTokenResponseDto;
import com.chillmo.skatedb.user.registration.exception.TokenExpiredException;
import com.chillmo.skatedb.user.registration.exception.TokenNotFoundException;
import com.chillmo.skatedb.user.registration.service.ConfirmationTokenService;
import com.chillmo.skatedb.util.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ConfirmationController.class)
@AutoConfigureMockMvc(addFilters = false)  // falls Security-Filter aktiv sind
class ConfirmationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Alle Abhängigkeiten des ConfirmationController mocken:
    @MockBean
    private ConfirmationTokenService tokenService;
    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Falls dein Controller noch weitere Beans im Konstruktor hat, hier ebenfalls @MockBean hinzufügen:
    // @MockBean private UserService userService;
    // @MockBean private EmailService emailService;

    @Test
    @DisplayName("GET /api/token/confirm?token=… — erfolgreicher Confirm → 200 + OK-Message")
    void whenValidConfirmToken_thenOk() throws Exception {
        String token = "valid-token";
        String okMessage = "E-Mail erfolgreich bestätigt!";
        when(tokenService.confirmToken(token)).thenReturn(true);

        mockMvc.perform(get("/api/token/confirm")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string(okMessage));

        verify(tokenService).confirmToken(token);
    }

    @Test
    @DisplayName("GET /api/confirm?token=… — abgelaufener Token → 400 Bad Request")
    void whenConfirmTokenExpired_thenBadRequest() throws Exception {
        String token = "expired-token";
        when(tokenService.confirmToken(token))
                .thenThrow(new TokenExpiredException(token));

        mockMvc.perform(get("/api/token/confirm")
                        .param("token", token))
                .andExpect(status().isBadRequest());

        verify(tokenService).confirmToken(token);
    }

    @Test
    @DisplayName("GET /api/token/confirm?token=… — Token nicht gefunden → 404 Not Found")
    void whenConfirmTokenNotFound_thenNotFound() throws Exception {
        String token = "unknown-token";
        when(tokenService.confirmToken(token))
                .thenThrow(new TokenNotFoundException(token));

        mockMvc.perform(get("/api/token/confirm")
                        .param("token", token))
                .andExpect(status().isNotFound());

        verify(tokenService).confirmToken(token);
    }

    /* ---------------------------------------------------
       Beispiel für den Renew-Endpunkt POST /api/confirm/renew?token=…
       --------------------------------------------------- */
    @Test
    @DisplayName("POST /api/token/renew — erfolgreich → 200 + neuer Token im JSON")
    void whenRenewValid_thenReturnNewToken() throws Exception {
        String oldToken = "old-token";
        ConfirmationTokenResponseDto dto = new ConfirmationTokenResponseDto();
        dto.setToken("new-token-xyz");
        dto.setExpiresAt(LocalDateTime.now().plusHours(24));

        when(tokenService.renewToken(oldToken)).thenReturn(dto);

        mockMvc.perform(post("/api/token/renew")
                        .param("token", oldToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new-token-xyz"))
                .andExpect(jsonPath("$.expiresAt").exists());

        verify(tokenService).renewToken(oldToken);
    }

    @Test
    @DisplayName("POST /api/token/renew — Token nicht gefunden → 404")
    void whenRenewTokenNotFound_thenNotFound() throws Exception {
        String oldToken = "does-not-exist";
        when(tokenService.renewToken(oldToken))
                .thenThrow(new TokenNotFoundException(oldToken));

        mockMvc.perform(post("/api/token/renew")
                        .param("token", oldToken))
                .andExpect(status().isNotFound());

        verify(tokenService).renewToken(oldToken);
    }

    @Test
    @DisplayName("POST /api/token/renew — abgelaufener Token → 400")
    void whenRenewTokenExpired_thenBadRequest() throws Exception {
        String oldToken = "expired-token";
        when(tokenService.renewToken(oldToken))
                .thenThrow(new TokenExpiredException(oldToken));

        mockMvc.perform(post("/api/token/renew")
                        .param("token", oldToken))
                .andExpect(status().isBadRequest());

        verify(tokenService).renewToken(oldToken);
    }
}