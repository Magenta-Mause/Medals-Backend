package com.medals.medalsbackend.service.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.otc")
public record OneTimeCodeConfiguration(long setPasswordTokenValidityDuration) {
}
