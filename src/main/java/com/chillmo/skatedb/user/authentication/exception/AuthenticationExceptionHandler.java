package com.chillmo.skatedb.user.authentication.exception;

import com.chillmo.skatedb.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UsernameNotFoundException ex) {
        ErrorResponse err = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabled(DisabledException ex) {
        ErrorResponse err = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(err);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        ErrorResponse err = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLocked(LockedException ex) {
        ErrorResponse err = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.LOCKED.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.LOCKED).body(err);
    }

    @ExceptionHandler(AccountExpiredException.class)
    public ResponseEntity<ErrorResponse> handleAccountExpired(AccountExpiredException ex) {
        ErrorResponse err = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<ErrorResponse> handleCredentialsExpired(CredentialsExpiredException ex) {
        ErrorResponse err = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        ErrorResponse err = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
    }
}