package com.chillmo.skatedb.user.service;

import com.chillmo.skatedb.user.domain.ExperienceLevel;
import com.chillmo.skatedb.user.domain.Role;
import com.chillmo.skatedb.user.domain.Stand;
import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.dto.ChangePasswordRequest;
import com.chillmo.skatedb.user.dto.UpdateProfileRequest;
import com.chillmo.skatedb.user.dto.UpdateUserRolesRequest;
import com.chillmo.skatedb.user.exception.InvalidPasswordException;
import com.chillmo.skatedb.user.exception.UserNotFoundException;
import com.chillmo.skatedb.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void updateProfileUpdatesProvidedFields() {
        User user = User.builder()
                .id(1L)
                .username("skater")
                .bio("old bio")
                .location("Munich")
                .experienceLevel(ExperienceLevel.NOVICE)
                .stand(Stand.Regular)
                .build();

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setBio("new bio");
        request.setLocation("Berlin");
        request.setExperienceLevel(ExperienceLevel.PRO);
        request.setStand(Stand.Goofy);
        request.setProfilePictureUrl("https://example.com/avatar.png");

        when(userRepository.findByUsername("skater")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updated = userService.updateProfile("skater", request);

        assertThat(updated.getBio()).isEqualTo("new bio");
        assertThat(updated.getLocation()).isEqualTo("Berlin");
        assertThat(updated.getExperienceLevel()).isEqualTo(ExperienceLevel.PRO);
        assertThat(updated.getStand()).isEqualTo(Stand.Goofy);
        assertThat(updated.getProfilePictureUrl()).isEqualTo("https://example.com/avatar.png");
        verify(userRepository).save(user);
    }

    @Test
    void updateProfileThrowsWhenUserMissing() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        UpdateProfileRequest request = new UpdateProfileRequest();
        assertThrows(UserNotFoundException.class, () -> userService.updateProfile("unknown", request));
    }

    @Test
    void changePasswordUpdatesWhenCurrentMatches() {
        User user = User.builder()
                .id(2L)
                .username("skater")
                .password("encoded-old")
                .build();

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPass123");
        request.setNewPassword("newPass123");

        when(userRepository.findByUsername("skater")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass123", "encoded-old")).thenReturn(true);
        when(passwordEncoder.encode("newPass123")).thenReturn("encoded-new");

        userService.changePassword("skater", request);

        assertThat(user.getPassword()).isEqualTo("encoded-new");
        verify(userRepository).save(user);
    }

    @Test
    void changePasswordRejectsInvalidCurrentPassword() {
        User user = User.builder()
                .id(2L)
                .username("skater")
                .password("encoded-old")
                .build();

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrong");
        request.setNewPassword("newPass123");

        when(userRepository.findByUsername("skater")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded-old")).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> userService.changePassword("skater", request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteAccountRemovesUser() {
        User user = User.builder()
                .id(3L)
                .username("skater")
                .build();

        when(userRepository.findByUsername("skater")).thenReturn(Optional.of(user));

        userService.deleteAccount("skater");

        verify(userRepository).delete(user);
    }

    @Test
    void deleteAccountThrowsWhenUserMissing() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteAccount("missing"));
        verify(userRepository, never()).delete(any());
    }

    @Test
    void updateRolesReplacesExistingRoles() {
        User user = User.builder()
                .id(4L)
                .username("skater")
                .roles(Set.of(Role.ROLE_USER))
                .build();

        UpdateUserRolesRequest request = new UpdateUserRolesRequest();
        request.setRoles(Set.of(Role.ROLE_ADMIN));

        when(userRepository.findById(4L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updated = userService.updateUserRoles(4L, request);

        assertThat(updated.getRoles()).containsExactlyInAnyOrder(Role.ROLE_ADMIN);
        verify(userRepository).save(user);
    }

    @Test
    void updateRolesThrowsWhenUserMissing() {
        UpdateUserRolesRequest request = new UpdateUserRolesRequest();
        request.setRoles(Set.of(Role.ROLE_ADMIN));

        when(userRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserRoles(7L, request));
    }
}
