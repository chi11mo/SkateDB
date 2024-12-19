package com.chillmo.skatedb.controller;

import com.chillmo.skatedb.dto.UserDTO;
import com.chillmo.skatedb.dto.UserLoginRequest;
import com.chillmo.skatedb.dto.UserRegisterRequest;
import com.chillmo.skatedb.exception.InvalidCredentialsException;
import com.chillmo.skatedb.exception.UserAlreadyExistsException;
import com.chillmo.skatedb.exception.InvalidTokenException;
import com.chillmo.skatedb.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user.
     *
     * Returns 200 OK and the created user if successful.
     * If the user already exists, throws UserAlreadyExistsException, which results in HTTP 409 CONFLICT.
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserRegisterRequest registerRequest) {
        UserDTO createdUser = authService.registerUser(registerRequest);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * Login with username/email and password.
     *
     * Returns 200 OK and a JWT token if credentials are valid.
     * If credentials are invalid, throws InvalidCredentialsException, resulting in HTTP 401 UNAUTHORIZED.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest loginRequest) {
        String jwtToken = authService.loginUser(loginRequest);
        return ResponseEntity.ok(jwtToken);
    }

    /**
     * Logout endpoint.
     *
     * Expects a JSON body with a "token" field:
     * {
     *   "token": "eyJhbGciOi..."
     * }
     *
     * If the token is valid and logout is successful, returns 200 OK.
     * If the token is invalid, throws InvalidTokenException, resulting in HTTP 401 UNAUTHORIZED.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        authService.logoutUser(token);
        return ResponseEntity.ok().build();
    }
}