package com.medals.medalsbackend.service.notifications.mail;

import com.medals.medalsbackend.config.MailTemplateConfiguration;
import com.medals.medalsbackend.entity.users.Trainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@RequiredArgsConstructor
@Service
@EnableConfigurationProperties(MailTemplateConfiguration.class)
public class MailTemplateService {
    private final MailTemplateConfiguration configuration;
    private final TemplateEngine templateEngine;

    public String generateSetPasswordNotification(String email, String link, String trainer) {
        Context context = new Context();
        context.setVariable("otcLink", link);
        context.setVariable("invitingTrainerName", trainer);
        return templateEngine.process(configuration.getSetPasswordNotification(), context);
    }

    public String generateInviteTrainerNotification(String email, String link, String admin) {
        Context context = new Context();
        context.setVariable("otcLink", link);
        context.setVariable("invitingAdminName", admin);
        return templateEngine.process(configuration.getInviteTrainerNotification(), context);
    }

    public String generatePasswordResetNotification(String email, String link) {
        Context context = new Context();
        context.setVariable("otcLink", link);
        return templateEngine.process(configuration.getResetPasswordNotification(), context);
    }

    public String generatePasswordResetConfirmationNotification(String email) {
        Context context = new Context();
        return templateEngine.process(configuration.getResetPasswordConfirmationNotification(), context);
    }

    public String generateRequestAthleteAccessNotification(String link, String trainer) {
        Context context = new Context();
        context.setVariable("otcLink", link);
        context.setVariable("trainerName", trainer);
        return templateEngine.process(configuration.getTrainerRequestAthleteAccessNotification(), context);
    }

    public String generateRoleAddedNotification(String accountCreatorRole, String creatorName, String targetRole) {
        Context context = new Context();
        context.setVariable("accountCreatorRole", accountCreatorRole);
        context.setVariable("creatorName", creatorName);
        context.setVariable("targetRole", targetRole);
        return templateEngine.process(configuration.getRoleAddedNotification(), context);
    }
}
