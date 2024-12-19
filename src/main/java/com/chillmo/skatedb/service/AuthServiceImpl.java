package com.chillmo.skatedb.service.impl;

import com.chillmo.skatedb.dto.UserDTO;
import com.chillmo.skatedb.dto.UserLoginRequest;
import com.chillmo.skatedb.dto.UserRegisterRequest;
import com.chillmo.skatedb.entity.Role;
import com.chillmo.skatedb.entity.User;
import com.chillmo.skatedb.exception.InvalidLoginException;
import com.chillmo.skatedb.exception.UserAlreadyExistsException;
import com.chillmo.skatedb.repository.UserRepository;
import com.chillmo.skatedb.service.AuthService;
import com.chillmo.skatedb.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;   // You can use BCryptPasswordEncoder
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public UserDTO registerUser(UserRegisterRequest registerRequest) {
        // Check if user exists by username or email
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email is already taken");
        }

        // Create a new User entity
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(Set.of(Role.ROLE_USER)) // Assign default role
                .build();

        // Save user in DB
        User savedUser = userRepository.save(user);

        // Return DTO
        return mapToDTO(savedUser);
    }

    @Override
    public String loginUser(UserLoginRequest loginRequest) {
        // Check whether loginRequest.usernameOrEmail matches username or email
        User user = userRepository.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail())
                .orElseThrow(() -> new InvalidLoginException("Invalid username"+ loginRequest.getUsernameOrEmail()+" or password"));

        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidLoginException("Invalid username"+ loginRequest.getUsernameOrEmail()+" or password");
        }

        // Generate JWT
        return jwtUtils.generateToken(user);
    }

    @Override
    public void logoutUser(String token) {
        //TODO Logout delte or Timeout Token
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .build();
    }
}