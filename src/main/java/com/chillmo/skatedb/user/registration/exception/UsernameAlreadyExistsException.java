package com.chillmo.skatedb.user.registration.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("Username bereits vergeben: " + username);
    }
}
