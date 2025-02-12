package com.chillmo.skatedb.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity // Ermöglicht methodenbasierte Sicherheitsregeln mit @PreAuthorize etc.
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Deaktiviert CSRF für stateless APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/api/**", "/api/register", "/api/trick-library/**").permitAll() // Auth- und Registrierungs-Endpunkte
                        .requestMatchers("/tricks/**").authenticated() // Absicherung der Tricks-API
                        .anyRequest().authenticated() // Alle anderen Anfragen erfordern Authentifizierung
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT-Filter einfügen
                //.httpBasic(Customizer.withDefaults()) // Optional: Basis-HTTP-Authentifizierung
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}