package com.medals.medalsbackend.service.user;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin")
public record AdminCreationConfiguration(boolean enabled, String adminEmail, String adminFirstName, String adminLastName)  {
}
