package com.chillmo.skatedb.user.registration.service;


import com.chillmo.skatedb.user.registration.domain.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    /**
     * Find a confirmation token by its string value.
     *
     * @param token token value
     * @return optional token entity
     */
    Optional<ConfirmationToken> findByToken(String token);
}