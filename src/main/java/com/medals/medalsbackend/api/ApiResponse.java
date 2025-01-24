package com.medals.medalsbackend.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ApiResponse<T> {
    private final ApiStatus status;
    private final HttpStatus httpStatus;
    private final String message;
    private final LocalDateTime timestamp;
    private final T data;

    private ApiResponse(ApiStatus status, HttpStatus httpStatus, String message, LocalDateTime timestamp, T data) {
        this.status = status;
        this.httpStatus = httpStatus;
        this.message = message;
        this.timestamp = timestamp;
        this.data = data;
    }

    public static <T> Builder<T> ok() {
        return new Builder<>(HttpStatus.OK);
    }

    public static <T> Builder<T> error(HttpStatus status) {
        return new Builder<>(status);
    }

    public ResponseEntity<Object> toResponseEntity() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("message", message);
        body.put("timestamp", timestamp);
        body.put("data", data);

        return ResponseEntity
                .status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    public static class Builder<T> {
        private final HttpStatus httpStatus;
        private ApiStatus apiStatus;
        private String message;
        private T data;

        public Builder(HttpStatus status) {
            this.httpStatus = status;
        }

        public Builder<T> status(ApiStatus status) {
            this.apiStatus = status;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ApiResponse<T> build() {
            if (apiStatus == null)
                apiStatus = ApiStatus.REFER_HTTP_STATUS;
            return new ApiResponse<>(apiStatus, httpStatus, message, LocalDateTime.now(), data);
        }
    }
}
