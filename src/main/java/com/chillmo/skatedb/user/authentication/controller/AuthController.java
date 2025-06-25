package com.chillmo.skatedb.user.authentication.controller;

import com.chillmo.skatedb.security.JwtResponseDto;
import com.chillmo.skatedb.user.authentication.dto.LoginRequestDto;
import com.chillmo.skatedb.user.domain.CustomUserDetails;
import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.util.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    /**
     * Authenticate a user and return a JWT token.
     *
     * @param req login credentials
     * @return JWT token if authentication succeeds
     */
    public ResponseEntity<JwtResponseDto> login(@Valid @RequestBody LoginRequestDto req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getIdentifier(), req.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        String token = jwtUtils.generateToken(user);

        return ResponseEntity.ok(new JwtResponseDto(token));
    }
}