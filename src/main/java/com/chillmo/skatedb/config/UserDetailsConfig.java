package com.chillmo.skatedb.config;

import com.chillmo.skatedb.user.domain.CustomUserDetails;
import com.chillmo.skatedb.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserDetailsConfig {

    private final UserRepository userRepository;

    public UserDetailsConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return identifier ->
                userRepository.findByIdentifier(identifier)
                        .map(CustomUserDetails::new)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found: " + identifier)
                        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}