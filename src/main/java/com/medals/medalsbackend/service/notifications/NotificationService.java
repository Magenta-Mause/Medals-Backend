package com.medals.medalsbackend.service.notifications;

import com.medals.medalsbackend.service.notifications.mail.MailService;
import com.medals.medalsbackend.service.notifications.mail.MailTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(NotificationServiceConfiguration.class)
public class NotificationService {

    private final NotificationServiceConfiguration configuration;
    private final MailService mailService;
    private final MailTemplateService templateService;

    public void sendResetPasswordNotification(String email, String oneTimeCode) {
        String link = configuration.getFrontEndBaseUrl() + "/resetPassword?oneTimeCode=" + oneTimeCode;
        String text = templateService.generatePasswordResetNotification(email, link);
        mailService.sendEmail(email, "Medals - Reset Password", text);
    }

    public void sendCreateAccountNotification(String email, String oneTimeCode) {
        String link = configuration.getFrontEndBaseUrl() + "/setPassword?oneTimeCode=" + oneTimeCode;
        String text = templateService.generateSetPasswordNotification(email, link);
        mailService.sendEmail(email, "Medals - Account Creation", text);
    }

    public void sendInviteTrainerNotification(String email, String oneTimeCode) {
        String link = configuration.getFrontEndBaseUrl() + "/setPassword?oneTimeCode=" + oneTimeCode;
        String text = templateService.generateInviteTrainerNotification(email, link);
        mailService.sendEmail(email, "Medals - Trainer Creation", text);
    }

    public void sendPasswordResetNotification(String email) {
        String text = templateService.generatePasswordResetConfirmationNotification(email);
        mailService.sendEmail(email, "Medals - Password Changed", text);
    }
}
