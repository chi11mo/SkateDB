package com.chillmo.skatedb.controller;

import com.chillmo.skatedb.dto.UserDTO;
import com.chillmo.skatedb.dto.UserLoginRequest;
import com.chillmo.skatedb.dto.UserRegisterRequest;
import com.chillmo.skatedb.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * @param registerRequest request body containing username, email, password.
     * @return the created UserDTO without password
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserRegisterRequest registerRequest) {
        UserDTO createdUser = authService.registerUser(registerRequest);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * Login with username/email and password.
     *
     * @param loginRequest request body containing username/email and password.
     * @return a JWT token or another token format
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest loginRequest) {
        String jwtToken = authService.loginUser(loginRequest);
        return ResponseEntity.ok(jwtToken);
    }

    /**
     * Optionally add a logout endpoint if you handle stateful sessions or invalidation of tokens.
     * For JWT (stateless), logout might just be handled client-side by discarding the token.
     */
    // @PostMapping("/logout")
    // public ResponseEntity<Void> logout(HttpServletRequest request) {
    //     authService.logoutUser(request);
    //     return ResponseEntity.ok().build();
    // }
}