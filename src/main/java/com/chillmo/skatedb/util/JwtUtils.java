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
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private Long jwtExpirationMs;

    // Erzeugt einen SecretKey aus dem jwtSecret
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Token erstellen
    public String generateToken(User user) {
        SecretKey key = getSigningKey();

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getRoles().stream().map(Enum::name).collect(Collectors.toSet())) // Rollen ins Token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Username aus Token extrahieren
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Rollen aus Token extrahieren
    public Set<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Extrahiere die Rollen als Liste und konvertiere sie in ein Set
        java.util.List<String> roles = claims.get("roles", java.util.List.class);
        return roles.stream().collect(Collectors.toSet());
    }

    // Token validieren
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            // Token ungültig
            return false;
        }
    }
}