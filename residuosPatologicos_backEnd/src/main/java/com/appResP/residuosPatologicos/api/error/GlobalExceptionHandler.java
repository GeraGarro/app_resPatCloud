package com.appResP.residuosPatologicos.api.error;

import com.appResP.residuosPatologicos.api.error.exceptions.DuplicateResourceException;
import com.appResP.residuosPatologicos.api.error.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
        return buildError(HttpStatus.UNAUTHORIZED, "Credenciales o rol invalidos", req.getRequestURI(), null);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabled(DisabledException ex, HttpServletRequest req) {
        return buildError(HttpStatus.FORBIDDEN, ex.getMessage(), req.getRequestURI(), null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI(), null);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex, HttpServletRequest req) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI(), null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI(), null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateResourceException ex, HttpServletRequest req) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

        Map<String, Object> details = Map.of("fieldErrors", fieldErrors);

        return buildError(HttpStatus.BAD_REQUEST, "Validation failed", req.getRequestURI(), details);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception on {} {}", req.getMethod(), req.getRequestURI(), ex);

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error occurred",
                req.getRequestURI(),
                null
        );
    }

    private ResponseEntity<ApiError> buildError(
            HttpStatus status,
            String message,
            String path,
            Map<String, Object> details
    ) {
        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                details
        );
        return ResponseEntity.status(status).body(body);
    }
}
