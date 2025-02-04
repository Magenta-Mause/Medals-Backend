package com.medals.medalsbackend.service.mail.mailApi;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.email")
public record MailServiceConfiguration(String author) {
}
