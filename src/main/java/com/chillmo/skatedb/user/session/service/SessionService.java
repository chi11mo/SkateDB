package com.chillmo.skatedb.user.session.service;

import com.chillmo.skatedb.exception.InvalidTokenException;
import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.exception.UserNotFoundException;
import com.chillmo.skatedb.user.repository.UserRepository;
import com.chillmo.skatedb.user.session.domain.RefreshToken;
import com.chillmo.skatedb.user.session.dto.SessionTokens;
import com.chillmo.skatedb.user.session.repository.RefreshTokenRepository;
import com.chillmo.skatedb.util.JwtUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class SessionService {

    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RevokedTokenService revokedTokenService;
    private final UserRepository userRepository;

    public SessionService(JwtUtils jwtUtils,
                          RefreshTokenRepository refreshTokenRepository,
                          RevokedTokenService revokedTokenService,
                          UserRepository userRepository) {
        this.jwtUtils = jwtUtils;
        this.refreshTokenRepository = refreshTokenRepository;
        this.revokedTokenService = revokedTokenService;
        this.userRepository = userRepository;
    }

    @Transactional
    public SessionTokens startSession(User user) {
        return issueTokens(user, true);
    }

    @Transactional
    public SessionTokens refreshSession(String refreshToken) {
        if (!jwtUtils.validateToken(refreshToken) || !jwtUtils.isRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Refresh token is invalid or expired");
        }

        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("Refresh token does not exist"));

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(stored);
            throw new InvalidTokenException("Refresh token has expired");
        }

        User user = stored.getUser();
        refreshTokenRepository.delete(stored);

        return issueTokens(user, false);
    }

    @Transactional
    public void logout(String username, String accessToken) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        refreshTokenRepository.deleteAllByUser(user);

        if (StringUtils.hasText(accessToken) && jwtUtils.validateToken(accessToken) && jwtUtils.isAccessToken(accessToken)) {
            revokedTokenService.revokeToken(accessToken, jwtUtils.getExpirationInstant(accessToken));
        }
    }

    private SessionTokens issueTokens(User user, boolean updateLastLogin) {
        refreshTokenRepository.deleteAllByUser(user);

        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        RefreshToken entity = RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .expiresAt(jwtUtils.getExpirationInstant(refreshToken))
                .build();

        refreshTokenRepository.save(entity);

        if (updateLastLogin) {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        }

        return new SessionTokens(accessToken, refreshToken, calculateAccessTokenTtl(accessToken));
    }

    private long calculateAccessTokenTtl(String accessToken) {
        Instant expiresAt = jwtUtils.getExpirationInstant(accessToken);
        long ttl = Duration.between(Instant.now(), expiresAt).toMillis();
        return Math.max(ttl, 0);
    }
}

