package com.chillmo.skatedb.user.registration.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TokenNotFoundException extends RuntimeException {



        public TokenNotFoundException(String token) {
            super("Token not found or already used: " + token);
        }
    }
