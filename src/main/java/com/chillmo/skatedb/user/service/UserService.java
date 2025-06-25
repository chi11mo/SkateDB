package com.chillmo.skatedb.user.service;
import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.email.service.EmailService;
import com.chillmo.skatedb.user.registration.domain.ConfirmationToken;
import com.chillmo.skatedb.user.registration.dto.UserRegistrationDto;
import com.chillmo.skatedb.user.registration.service.ConfirmationTokenRepository;
import com.chillmo.skatedb.user.registration.service.ConfirmationTokenService;
import com.chillmo.skatedb.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ConfirmationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private final ConfirmationTokenService confirmationTokenService;

    public UserService(UserRepository userRepository,
                       ConfirmationTokenRepository tokenRepository,
                       EmailService emailService,
                       PasswordEncoder passwordEncoder, ConfirmationTokenService confirmationTokenService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenService = confirmationTokenService;
    }

    /**
     * Retrieve all users from the database.
     *
     * @return list of users
     */
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
