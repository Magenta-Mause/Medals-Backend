package com.medals.medalsbackend.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record JwtConfigurationProperties(String secretKey, long refreshTokenExpirationTime, long identityTokenExpirationTime, long inviteTokenExpirationTime) {
}
