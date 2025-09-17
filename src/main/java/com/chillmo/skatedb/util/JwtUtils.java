package com.chillmo.skatedb.util;

import com.chillmo.skatedb.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.accessExpirationMs:${jwt.expirationMs}}")
    private Long accessExpirationMs;

    @Value("${jwt.refreshExpirationMs:604800000}")
    private Long refreshExpirationMs;

    // Create a signing key from the configured secret
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String buildToken(User user, long expirationMs, JwtTokenType tokenType) {
        SecretKey key = getSigningKey();

        Set<String> roles = user.getRoles() == null
                ? Collections.emptySet()
                : user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", roles)
                .claim("type", tokenType.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate a JWT token for the given user.
     *
     * @param user authenticated user
     * @return signed JWT token
     */
    public String generateAccessToken(User user) {
        return buildToken(user, accessExpirationMs, JwtTokenType.ACCESS);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, refreshExpirationMs, JwtTokenType.REFRESH);
    }

    // Extract the username from the token
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    // Extract the roles from the token
    public Set<String> getRolesFromToken(String token) {
        Claims claims = parseClaims(token);

        // Extract the roles as list and convert to a Set
        java.util.List<String> roles = claims.get("roles", java.util.List.class);
        if (roles == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(roles);
    }

    // Validate the token
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException ex) {
            // Token is invalid
            return false;
        }
    }

    public JwtTokenType getTokenType(String token) {
        Claims claims = parseClaims(token);
        String type = claims.get("type", String.class);
        if (type == null) {
            return JwtTokenType.ACCESS;
        }
        return JwtTokenType.valueOf(type);
    }

    public boolean isAccessToken(String token) {
        return getTokenType(token) == JwtTokenType.ACCESS;
    }

    public boolean isRefreshToken(String token) {
        return getTokenType(token) == JwtTokenType.REFRESH;
    }

    public Instant getExpirationInstant(String token) {
        return parseClaims(token).getExpiration().toInstant();
    }

    public long getAccessTokenTtlMillis() {
        return accessExpirationMs;
    }

    public long getRefreshTokenTtlMillis() {
        return refreshExpirationMs;
    }
}