package com.medals.medalsbackend.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> internalErrorHandler(Exception ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body("Internal Server Error");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e, WebRequest request) {
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, fieldError -> {
            String defaultMessage = fieldError.getDefaultMessage();
            return defaultMessage != null ? defaultMessage : "No error message available";
        }));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<String> handleMissingCookieException(MissingRequestCookieException e, WebRequest request) {
        return ResponseEntity.badRequest().body("Missing Cookie '" + e.getCookieName() + "'");
    }

    @ExceptionHandler(GenericAPIRequestException.class)
    public ResponseEntity<String> genericAPIRequestException(GenericAPIRequestException ex, WebRequest request) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
    }
}
