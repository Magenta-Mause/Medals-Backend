package com.medals.medalsbackend.exception;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleUnrecognizedPropertyException(HttpMessageNotReadableException ex, WebRequest request) {
        try {
            UnrecognizedPropertyException exception = (UnrecognizedPropertyException) ex.getCause();
            return ResponseEntity.badRequest().body("Malformed Json, invalid property: '" + exception.getPropertyName() + "'");
        } catch (Exception ignored) {
            return ResponseEntity.badRequest().body("Message not readable");
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e, WebRequest request) {
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, fieldError -> {
            String defaultMessage = fieldError.getDefaultMessage();
            return defaultMessage != null ? defaultMessage : "No error message available";
        }));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<String> handleMissingCookieException(MissingRequestCookieException e, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing Cookie '" + e.getCookieName() + "'");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing Parameter '" + e.getParameterName() + "'");
    }

    @ExceptionHandler(GenericAPIRequestException.class)
    public ResponseEntity<String> genericAPIRequestException(GenericAPIRequestException ex, WebRequest request) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
    }
}
