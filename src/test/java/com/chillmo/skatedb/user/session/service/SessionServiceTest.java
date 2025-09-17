package com.chillmo.skatedb.user.session.service;

import com.chillmo.skatedb.exception.InvalidTokenException;
import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.repository.UserRepository;
import com.chillmo.skatedb.user.session.domain.RefreshToken;
import com.chillmo.skatedb.user.session.dto.SessionTokens;
import com.chillmo.skatedb.user.session.repository.RefreshTokenRepository;
import com.chillmo.skatedb.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private RevokedTokenService revokedTokenService;
    @Mock
    private UserRepository userRepository;

    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService(jwtUtils, refreshTokenRepository, revokedTokenService, userRepository);
    }

    @Test
    void startSessionGeneratesAndPersistsTokens() {
        User user = User.builder().id(1L).username("skater").build();
        Instant refreshExpiry = Instant.now().plusSeconds(3600);
        Instant accessExpiry = Instant.now();

        when(jwtUtils.generateAccessToken(user)).thenReturn("access-token");
        when(jwtUtils.generateRefreshToken(user)).thenReturn("refresh-token");
        when(jwtUtils.getExpirationInstant("refresh-token")).thenReturn(refreshExpiry);
        when(jwtUtils.getExpirationInstant("access-token")).thenReturn(accessExpiry);
        when(userRepository.save(user)).thenReturn(user);

        SessionTokens tokens = sessionService.startSession(user);

        assertThat(tokens.accessToken()).isEqualTo("access-token");
        assertThat(tokens.refreshToken()).isEqualTo("refresh-token");
        assertThat(tokens.accessTokenExpiresIn()).isZero();

        verify(refreshTokenRepository).deleteAllByUser(user);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        RefreshToken stored = captor.getValue();
        assertThat(stored.getToken()).isEqualTo("refresh-token");
        assertThat(stored.getUser()).isEqualTo(user);
        assertThat(stored.getExpiresAt()).isEqualTo(refreshExpiry);
        verify(userRepository).save(user);
    }

    @Test
    void refreshSessionRotatesTokens() {
        User user = User.builder().id(2L).username("skater").build();
        RefreshToken existing = RefreshToken.builder()
                .token("refresh-token")
                .user(user)
                .expiresAt(Instant.now().plusSeconds(60))
                .build();

        when(jwtUtils.validateToken("refresh-token")).thenReturn(true);
        when(jwtUtils.isRefreshToken("refresh-token")).thenReturn(true);
        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(Optional.of(existing));
        when(jwtUtils.generateAccessToken(user)).thenReturn("new-access");
        when(jwtUtils.generateRefreshToken(user)).thenReturn("new-refresh");
        when(jwtUtils.getExpirationInstant("new-refresh")).thenReturn(Instant.now().plusSeconds(120));
        when(jwtUtils.getExpirationInstant("new-access")).thenReturn(Instant.now());
        when(userRepository.save(user)).thenReturn(user);

        SessionTokens tokens = sessionService.refreshSession("refresh-token");

        assertThat(tokens.accessToken()).isEqualTo("new-access");
        assertThat(tokens.refreshToken()).isEqualTo("new-refresh");

        verify(refreshTokenRepository).delete(existing);
        verify(refreshTokenRepository).deleteAllByUser(user);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void refreshSessionRejectsUnknownToken() {
        when(jwtUtils.validateToken("unknown")).thenReturn(true);
        when(jwtUtils.isRefreshToken("unknown")).thenReturn(true);
        when(refreshTokenRepository.findByToken("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.refreshSession("unknown"))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void refreshSessionRemovesExpiredToken() {
        User user = User.builder().id(3L).username("skater").build();
        RefreshToken existing = RefreshToken.builder()
                .token("refresh-token")
                .user(user)
                .expiresAt(Instant.now().minusSeconds(5))
                .build();

        when(jwtUtils.validateToken("refresh-token")).thenReturn(true);
        when(jwtUtils.isRefreshToken("refresh-token")).thenReturn(true);
        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> sessionService.refreshSession("refresh-token"))
                .isInstanceOf(InvalidTokenException.class);

        verify(refreshTokenRepository).delete(existing);
    }

    @Test
    void refreshSessionRejectsInvalidJwt() {
        when(jwtUtils.validateToken("bad")).thenReturn(false);

        assertThatThrownBy(() -> sessionService.refreshSession("bad"))
                .isInstanceOf(InvalidTokenException.class);
        verifyNoInteractions(refreshTokenRepository);
    }

    @Test
    void logoutRevokesAccessTokenAndClearsRefreshTokens() {
        User user = User.builder().id(4L).username("skater").build();
        Instant expiry = Instant.now().plusSeconds(30);

        when(userRepository.findByUsername("skater")).thenReturn(Optional.of(user));
        when(jwtUtils.validateToken("access-token")).thenReturn(true);
        when(jwtUtils.isAccessToken("access-token")).thenReturn(true);
        when(jwtUtils.getExpirationInstant("access-token")).thenReturn(expiry);

        sessionService.logout("skater", "access-token");

        verify(refreshTokenRepository).deleteAllByUser(user);
        verify(revokedTokenService).revokeToken("access-token", expiry);
    }

    @Test
    void logoutSkipsRevocationForInvalidToken() {
        User user = User.builder().id(5L).username("skater").build();
        when(userRepository.findByUsername("skater")).thenReturn(Optional.of(user));
        when(jwtUtils.validateToken("invalid")).thenReturn(false);

        sessionService.logout("skater", "invalid");

        verify(refreshTokenRepository).deleteAllByUser(user);
        verifyNoInteractions(revokedTokenService);
    }
}

