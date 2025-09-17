package com.chillmo.skatedb.user.registration.service;

import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.registration.domain.ConfirmationToken;
import com.chillmo.skatedb.user.registration.exception.TokenExpiredException;
import com.chillmo.skatedb.user.registration.exception.TokenNotFoundException;
import com.chillmo.skatedb.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

    @Mock
    private ConfirmationTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ConfirmationTokenService confirmationTokenService;

    @Test
    void confirmTokenDeletesTokenAfterSuccessfulVerification() {
        var user = new User();
        user.setEnabled(false);

        var confirmationToken = ConfirmationToken.builder()
                .token("valid-token")
                .createdAt(LocalDateTime.now().minusMinutes(1))
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(confirmationToken));

        boolean result = confirmationTokenService.confirmToken("valid-token");

        assertTrue(result);
        assertTrue(user.getEnabled());
        verify(userRepository).save(user);
        verify(tokenRepository).delete(confirmationToken);
    }

    @Test
    void confirmTokenThrowsWhenTokenMissing() {
        when(tokenRepository.findByToken("missing-token")).thenReturn(Optional.empty());

        assertThrows(TokenNotFoundException.class, () -> confirmationTokenService.confirmToken("missing-token"));

        verify(tokenRepository).findByToken("missing-token");
        verifyNoMoreInteractions(tokenRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void confirmTokenThrowsWhenTokenExpired() {
        var user = new User();
        var confirmationToken = ConfirmationToken.builder()
                .token("expired-token")
                .createdAt(LocalDateTime.now().minusHours(2))
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .user(user)
                .build();

        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(confirmationToken));

        assertThrows(TokenExpiredException.class, () -> confirmationTokenService.confirmToken("expired-token"));

        verify(tokenRepository).findByToken("expired-token");
        verify(tokenRepository, never()).delete(any());
        verifyNoInteractions(userRepository);
    }
}
