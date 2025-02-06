package com.chillmo.skatedb.user.service;
import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.registration.dto.UserRegistrationDto;
import com.chillmo.skatedb.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRegistrationDto dto) {
        // Überprüfen, ob der Username oder die Email bereits existiert
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username ist bereits vergeben.");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email wird bereits verwendet.");
        }

        // Erstelle einen neuen User mit verschlüsseltem Passwort
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .experienceLevel(dto.getExperienceLevel())
                .build();

        // Speichere den User in der Datenbank
        return userRepository.save(user);
    }
}
