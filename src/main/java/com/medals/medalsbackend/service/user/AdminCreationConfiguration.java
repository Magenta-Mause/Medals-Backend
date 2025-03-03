package com.medals.medalsbackend.service.user;

import com.medals.medalsbackend.entity.users.Admin;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin")
public record AdminCreationConfiguration(boolean enabled, Admin[] admins)  {
}
