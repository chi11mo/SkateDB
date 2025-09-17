package com.chillmo.skatedb.user.session.repository;

import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.session.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteAllByUser(User user);
}

