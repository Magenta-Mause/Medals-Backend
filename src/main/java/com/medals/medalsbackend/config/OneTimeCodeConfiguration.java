package com.medals.medalsbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.otc")
public record OneTimeCodeConfiguration(long setPasswordTokenValidityDuration, long resetPasswordTokenValidityDuration, long validateInviteTokenDuration) {
}
