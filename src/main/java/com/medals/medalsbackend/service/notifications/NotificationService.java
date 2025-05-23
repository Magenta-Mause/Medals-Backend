package com.medals.medalsbackend.service.notifications;

import com.medals.medalsbackend.entity.users.Trainer;
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
        String link = configuration.getFrontendBaseUrl() + "/resetPassword?oneTimeCode=" + oneTimeCode;
        String text = templateService.generatePasswordResetNotification(email, link);
        mailService.sendEmail(email, "Medals - Reset Password", text);
    }

    public void sendCreateAccountNotification(String email, String oneTimeCode, String trainerName) {
        String link = configuration.getFrontendBaseUrl() + "/setPassword?oneTimeCode=" + oneTimeCode;
        String text = templateService.generateSetPasswordNotification(email, link, trainerName);
        mailService.sendEmail(email, "Medals - Account Creation", text);
    }

    public void sendInviteTrainerNotification(String email, String oneTimeCode, String adminName) {
        String link = configuration.getFrontendBaseUrl() + "/setPassword?oneTimeCode=" + oneTimeCode;
        String text = templateService.generateInviteTrainerNotification(email, link, adminName);
        mailService.sendEmail(email, "Medals - Trainer Creation", text);
    }

    public void sendPasswordResetNotification(String email) {
        String text = templateService.generatePasswordResetConfirmationNotification(email);
        mailService.sendEmail(email, "Medals - Password Changed", text);
    }

    public void sendRequestAthleteNotification(String email, String token, String trainer) {
        String link = configuration.getFrontendBaseUrl() + "/approve-request?oneTimeCode=" + token;
        String text = templateService.generateRequestAthleteAccessNotification(link, trainer);
        mailService.sendEmail(email, "Medals - Trainer requested access", text);
    }

    public void sendRoleAddedNotification(String email, String creatorName, String creatorRole, String targetRole) {
        String text = templateService.generateRoleAddedNotification(creatorRole, creatorName, targetRole);
        mailService.sendEmail(email, "Medals - Role Added", text);
    }
}
