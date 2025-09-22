package com.chillmo.skatedb.user.registration.service;

import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.email.service.EmailDomainValidator;
import com.chillmo.skatedb.user.email.service.EmailService;
import com.chillmo.skatedb.user.registration.domain.ConfirmationToken;
import com.chillmo.skatedb.user.registration.dto.UserRegistrationDto;
import com.chillmo.skatedb.user.registration.exception.EmailAlreadyExistsException;
import com.chillmo.skatedb.user.registration.exception.UsernameAlreadyExistsException;
import com.chillmo.skatedb.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final PasswordValidationService passwordValidationService;
    private final EmailRegisterService emailRegisterService;
    private final EmailDomainValidator emailDomainValidator;

    public UserRegistrationService(
            UserRepository userRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder,
            ConfirmationTokenService confirmationTokenService,
            PasswordValidationService passwordValidationService,
            EmailRegisterService emailRegisterService,
            EmailDomainValidator emailDomainValidator
    ) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenService = confirmationTokenService;
        this.passwordValidationService = passwordValidationService;
        this.emailRegisterService = emailRegisterService;
        this.emailDomainValidator = emailDomainValidator;
    }

    @Transactional
    public User registerUser(UserRegistrationDto dto) {
        // 1) Username- und E-Mail-Check
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new UsernameAlreadyExistsException(dto.getUsername());
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        emailDomainValidator.validateOrThrow(dto.getEmail());

        // 2) Passwort-Policy prüfen
        passwordValidationService.validate(dto.getPassword());

        // 3) User anlegen und persistieren
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                // ⚠️ encode erst NACH der Validierung
                .password(passwordEncoder.encode(dto.getPassword()))
                .experienceLevel(dto.getExperienceLevel())
                .enabled(false) // noch nicht aktiviert
                .build();
        User savedUser = userRepository.save(user);

        // 4) Token erzeugen (muss den persistierten User referenzieren!)
        ConfirmationToken token = confirmationTokenService.getNewConfirmationToken(savedUser);

        // 5) Bestätigungs-E-Mail versenden (ggf. asynchron)
        String body = emailRegisterService.createRegisterEmailBody(savedUser, token);
        emailService.sendAsync(
                savedUser.getEmail(),
                "E-Mail-Bestätigung für SkateDB",
                body
        );

        return savedUser;
    }
}
