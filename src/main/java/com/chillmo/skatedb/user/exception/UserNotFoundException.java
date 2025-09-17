package com.chillmo.skatedb.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User not found: " + id);
    }

    public UserNotFoundException(String identifier) {
        super("User not found: " + identifier);
    }
}
