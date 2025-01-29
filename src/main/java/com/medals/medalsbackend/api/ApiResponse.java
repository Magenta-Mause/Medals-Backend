package com.medals.medalsbackend.api;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

@Builder
@Data
public class ApiResponse<T> {
    private int status;
    private HttpStatusCode httpStatus;
    private String message;
    private String endpoint;
    private LocalDateTime timestamp;
    private T data;
}
