package com.chillmo.skatedb.user.registration.service;

import com.chillmo.skatedb.user.registration.exception.TokenExpiredException;
import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.registration.domain.ConfirmationToken;
import com.chillmo.skatedb.user.registration.dto.ConfirmationTokenResponseDto;
import com.chillmo.skatedb.user.registration.exception.TokenNotFoundException;
import com.chillmo.skatedb.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
@Transactional
@Service
public class ConfirmationTokenService {


    private final ConfirmationTokenRepository tokenRepository;

    private final UserRepository userRepository;

    public ConfirmationTokenService(ConfirmationTokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;

    }


    public ConfirmationToken getNewConfirmationToken(User user) {


        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .user(user)
                .build();
        tokenRepository.save(confirmationToken);

        return confirmationToken;
    }

    public void tokenExpired(ConfirmationToken confirmationToken) {
        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException(confirmationToken.getToken());
        }

    }

    @Transactional
    public ConfirmationTokenResponseDto renewToken(String expiredToken) {

        ConfirmationToken oldToken = tokenRepository.findByToken(expiredToken)
                .orElseThrow(() -> new TokenNotFoundException(expiredToken));


        tokenRepository.delete(oldToken);


        String newValue = UUID.randomUUID().toString();
        ConfirmationToken newToken = ConfirmationToken.builder()
                .token(newValue)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .user(oldToken.getUser())
                .build();
        tokenRepository.save(newToken);


        ConfirmationTokenResponseDto dto = new ConfirmationTokenResponseDto();
        dto.setToken(newValue);
        dto.setExpiresAt(newToken.getExpiresAt());
        return dto;
    }

    /**
     * Bestätigt einen Registrierungs-Token:
     * - wirft TokenNotFoundException, wenn der Token nicht existiert
     * - wirft TokenExpiredException, wenn er abgelaufen ist
     * - setzt user.enabled = true und speichert den User
     * - entfernt den Token
     *
     * @param token der Bestätigungs-Token
     * @return true, wenn die Bestätigung erfolgreich war
     */
    public boolean confirmToken(String token) {
        var confirmationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException(token));

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException(token);
        }

        var user = confirmationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        return true;
    }
}