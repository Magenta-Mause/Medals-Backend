package com.medals.medalsbackend.service.notifications.mail.mailApi;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.email")
public record MailServiceConfiguration(String author) {
}
