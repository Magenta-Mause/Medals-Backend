package com.medals.medalsbackend.service.onetimecode;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.otc")
public record OneTimeCodeConfiguration(long setPasswordTokenValidityDuration, long resetPasswordTokenValidityDuration) {
}
