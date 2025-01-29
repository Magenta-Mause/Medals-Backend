package com.medals.medalsbackend.exceptions;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(int status, LocalDateTime timestamp, Object error, String message, String path) {
}
