package com.chillmo.skatedb.user.session.service;

import com.chillmo.skatedb.user.session.domain.RevokedToken;
import com.chillmo.skatedb.user.session.repository.RevokedTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class RevokedTokenService {

    private final RevokedTokenRepository revokedTokenRepository;

    public RevokedTokenService(RevokedTokenRepository revokedTokenRepository) {
        this.revokedTokenRepository = revokedTokenRepository;
    }

    @Transactional
    public boolean isTokenRevoked(String token) {
        cleanupExpiredTokens();
        return revokedTokenRepository.existsByToken(token);
    }

    @Transactional
    public void revokeToken(String token, Instant expiresAt) {
        cleanupExpiredTokens();
        revokedTokenRepository.findByToken(token)
                .orElseGet(() -> revokedTokenRepository.save(
                        RevokedToken.builder()
                                .token(token)
                                .expiresAt(expiresAt)
                                .build()
                ));
    }

    private void cleanupExpiredTokens() {
        revokedTokenRepository.deleteAllByExpiresAtBefore(Instant.now());
    }
}

