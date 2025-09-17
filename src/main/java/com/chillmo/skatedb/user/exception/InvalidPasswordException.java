package com.chillmo.skatedb.user.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Current password does not match");
    }
}
