package com.medals.medalsbackend.service.notifications;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "app.notification")
public class NotificationServiceConfiguration {
    private String notificationServiceBaseUrl;
}
