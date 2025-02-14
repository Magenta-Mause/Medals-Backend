package com.medals.medalsbackend.exception;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(int status, LocalDateTime timestamp, Object error, String message, String path) {
}
