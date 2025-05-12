package com.chillmo.skatedb.user.registration.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email bereits verwendet: " + email);
    }
}
