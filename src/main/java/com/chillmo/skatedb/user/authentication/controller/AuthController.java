package com.chillmo.skatedb.user.authentication.controller;

import com.chillmo.skatedb.security.JwtResponseDto;
import com.chillmo.skatedb.user.authentication.dto.LoginRequestDto;
import com.chillmo.skatedb.user.authentication.dto.RefreshTokenRequest;
import com.chillmo.skatedb.user.domain.CustomUserDetails;
import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.session.dto.SessionTokens;
import com.chillmo.skatedb.user.session.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SessionService sessionService;

    public AuthController(AuthenticationManager authenticationManager,
                          SessionService sessionService) {
        this.authenticationManager = authenticationManager;
        this.sessionService = sessionService;
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
        SessionTokens tokens = sessionService.startSession(user);

        return ResponseEntity.ok(new JwtResponseDto(
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.accessTokenExpiresIn()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDto> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        SessionTokens tokens = sessionService.refreshSession(request.getRefreshToken());
        return ResponseEntity.ok(new JwtResponseDto(
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.accessTokenExpiresIn()
        ));
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(Authentication authentication,
                                       @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        sessionService.logout(authentication.getName(), resolveToken(authorizationHeader));
        return ResponseEntity.noContent().build();
    }

    private String resolveToken(String header) {
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}