package com.medals.medalsbackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Arrays;

@ControllerAdvice
public class CustomExceptionHandler {

    private String getPath(WebRequest request) {
        return Arrays.stream(request.getDescription(false).split("=")).toList().get(1);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> internalErrorHandler(Exception ex, WebRequest request) {
        return ResponseEntity.internalServerError().body(ErrorResponse.builder()
                .path(getPath(request))
                .error(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .message("Internal Server Error")
                .build());
    }

    @ExceptionHandler(GenericAPIRequestException.class)
    public ResponseEntity<ErrorResponse> genericAPIRequestException(GenericAPIRequestException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .path(getPath(request))
                .error(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .build());
    }
}
