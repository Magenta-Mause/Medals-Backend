package com.medals.medalsbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.email.templates")
public class MailTemplateConfiguration {
    private String setPasswordNotification;
    private String inviteTrainerNotification;
    private String trainerRequestAthleteAccessNotification;
    private String resetPasswordNotification;
    private String resetPasswordConfirmationNotification;
    private String accountAddedNotification;
}
