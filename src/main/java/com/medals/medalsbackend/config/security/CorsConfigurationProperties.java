package com.medals.medalsbackend.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cors")
public record CorsConfigurationProperties(String[] allowedOrigins) {
}
