package com.chillmo.skatedb.user;

import com.chillmo.skatedb.user.domain.ExperienceLevel;
import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.email.service.EmailService;
import com.chillmo.skatedb.user.registration.domain.ConfirmationToken;
import com.chillmo.skatedb.user.registration.dto.UserRegistrationDto;
import com.chillmo.skatedb.user.registration.exception.EmailAlreadyExistsException;
import com.chillmo.skatedb.user.registration.exception.UsernameAlreadyExistsException;
import com.chillmo.skatedb.user.registration.service.ConfirmationTokenService;
import com.chillmo.skatedb.user.registration.service.EmailRegisterService;
import com.chillmo.skatedb.user.registration.service.PasswordValidationService;
import com.chillmo.skatedb.user.registration.service.UserRegistrationService;
import com.chillmo.skatedb.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserRegistrationServiceTest {

    @Autowired
    private UserRepository userRepository;

    private EmailService emailService;
    private PasswordEncoder passwordEncoder;
    private ConfirmationTokenService confirmationTokenService;
    private PasswordValidationService passwordValidationService;
    private EmailRegisterService emailRegisterService;

    private UserRegistrationService userRegistrationService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userRepository.flush();

        emailService = mock(EmailService.class);
        passwordEncoder = mock(PasswordEncoder.class);
        confirmationTokenService = mock(ConfirmationTokenService.class);
        passwordValidationService = spy(new PasswordValidationService());
        emailRegisterService = mock(EmailRegisterService.class);

        userRegistrationService = new UserRegistrationService(
                userRepository,
                emailService,
                passwordEncoder,
                confirmationTokenService,
                passwordValidationService,
                emailRegisterService
        );
    }

    @Test
    void registerUser_persistsUserAndDispatchesConfirmationEmail() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("freshUser");
        dto.setEmail("fresh@example.com");
        dto.setPassword("StrongPass1!");
        dto.setExperienceLevel(ExperienceLevel.NOVICE);

        when(passwordEncoder.encode("StrongPass1!")).thenReturn("encoded-password");
        ConfirmationToken token = ConfirmationToken.builder().token("token-value").build();
        when(confirmationTokenService.getNewConfirmationToken(any(User.class))).thenReturn(token);
        when(emailRegisterService.createRegisterEmailBody(any(User.class), eq(token))).thenReturn("body");

        User savedUser = userRegistrationService.registerUser(dto);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("freshUser");
        assertThat(savedUser.getEmail()).isEqualTo("fresh@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("encoded-password");
        assertThat(savedUser.getEnabled()).isFalse();
        assertThat(userRepository.findById(savedUser.getId())).isPresent();

        verify(passwordValidationService).validate("StrongPass1!");
        verify(passwordEncoder).encode("StrongPass1!");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(confirmationTokenService).getNewConfirmationToken(userCaptor.capture());
        User tokenUser = userCaptor.getValue();
        assertThat(tokenUser.getId()).isEqualTo(savedUser.getId());

        verify(emailRegisterService).createRegisterEmailBody(same(savedUser), eq(token));
        verify(emailService).sendAsync(eq("fresh@example.com"), eq("E-Mail-Bestätigung für SkateDB"), eq("body"));
    }

    @Test
    void registerUser_whenUsernameExists_throwsException() {
        User existingUser = User.builder()
                .username("takenUser")
                .email("unique@example.com")
                .password("encoded")
                .enabled(true)
                .build();
        userRepository.saveAndFlush(existingUser);

        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("takenUser");
        dto.setEmail("fresh@example.com");
        dto.setPassword("StrongPass1!");

        assertThatThrownBy(() -> userRegistrationService.registerUser(dto))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("takenUser");

        verify(passwordValidationService, never()).validate(any());
        verifyNoInteractions(passwordEncoder, confirmationTokenService, emailRegisterService, emailService);
    }

    @Test
    void registerUser_whenEmailExists_throwsException() {
        User existingUser = User.builder()
                .username("anotherUser")
                .email("duplicate@example.com")
                .password("encoded")
                .enabled(true)
                .build();
        userRepository.saveAndFlush(existingUser);

        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("freshUser");
        dto.setEmail("duplicate@example.com");
        dto.setPassword("StrongPass1!");

        assertThatThrownBy(() -> userRegistrationService.registerUser(dto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("duplicate@example.com");

        verify(passwordValidationService, never()).validate(any());
        verifyNoInteractions(passwordEncoder, confirmationTokenService, emailRegisterService, emailService);
    }
}
