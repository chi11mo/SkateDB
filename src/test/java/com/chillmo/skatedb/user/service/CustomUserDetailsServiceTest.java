package com.chillmo.skatedb.user.service;

import com.chillmo.skatedb.user.domain.CustomUserDetails;
import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsernameSupportsUsernames() {
        User user = User.builder()
                .id(1L)
                .username("skater")
                .email("skater@example.com")
                .password("encoded")
                .build();

        when(userRepository.findByIdentifier("skater")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("skater");

        assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
        assertThat(userDetails.getUsername()).isEqualTo("skater");
        verify(userRepository).findByIdentifier("skater");
    }

    @Test
    void loadUserByUsernameSupportsEmailAddresses() {
        User user = User.builder()
                .id(2L)
                .username("skater")
                .email("skater@example.com")
                .password("encoded")
                .build();

        when(userRepository.findByIdentifier("skater@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("skater@example.com");

        assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
        assertThat(userDetails.getUsername()).isEqualTo("skater");
        verify(userRepository).findByIdentifier("skater@example.com");
    }

    @Test
    void loadUserByUsernameThrowsWhenUserMissing() {
        when(userRepository.findByIdentifier("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("missing@example.com"));
        verify(userRepository).findByIdentifier("missing@example.com");
    }
}
