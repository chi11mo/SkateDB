package com.chillmo.skatedb.user.registration.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String reason) {
        super(reason);
    }
}
