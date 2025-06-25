package com.chillmo.skatedb.user.registration.service;


import com.chillmo.skatedb.user.registration.exception.EmailAlreadyExistsException;
import com.chillmo.skatedb.user.registration.exception.InvalidPasswordException;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class PasswordValidationService {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$"
    );

    public void validate(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new InvalidPasswordException(
                    "Password must be at least 8 characters long and include at least one uppercase letter, " +
                            "one lowercase letter, one digit, and one special character."
            );
        }
    }

    /**
     * Check if the given password satisfies the policy without throwing.
     *
     * @param password password to check
     * @return true if valid according to the policy
     */
    public boolean isValid(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
}
