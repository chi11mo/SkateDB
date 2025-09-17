package com.chillmo.skatedb.user.session.repository;

import com.chillmo.skatedb.user.session.domain.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {
    Optional<RevokedToken> findByToken(String token);

    boolean existsByToken(String token);

    void deleteAllByExpiresAtBefore(Instant instant);
}

