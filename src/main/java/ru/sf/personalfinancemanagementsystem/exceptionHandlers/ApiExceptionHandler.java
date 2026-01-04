package ru.sf.personalfinancemanagementsystem.exceptionHandlers;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.sf.personalfinancemanagementsystem.exceptions.BadLoginOrPasswordException;
import ru.sf.personalfinancemanagementsystem.exceptions.UserAlreadyExistsException;
import ru.sf.personalfinancemanagementsystem.services.impl.AuthenticationServiceImpl;

import java.time.Instant;


@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleExists(@NotNull UserAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("USER_ALREADY_EXISTS", e.getMessage(), Instant.now()));
    }

    @ExceptionHandler(BadLoginOrPasswordException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(@NotNull BadLoginOrPasswordException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("BAD_CREDENTIALS", e.getMessage(), Instant.now()));
    }

    public record ErrorResponse(String code, String message, Instant timestamp) {}
}
