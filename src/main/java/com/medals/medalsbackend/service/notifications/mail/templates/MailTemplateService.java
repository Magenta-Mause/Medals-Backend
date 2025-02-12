package com.medals.medalsbackend.service.notifications.mail.templates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@RequiredArgsConstructor
@Service
@EnableConfigurationProperties(MailTemplateConfiguration.class)
public class MailTemplateService {
    private final MailTemplateConfiguration configuration;
    private final TemplateEngine templateEngine;

    public String generateSetPasswordNotification(String email, String link) {
        Context context = new Context();
        context.setVariable("otpLink", link);
        return templateEngine.process(configuration.getSetPasswordNotification(), context);
    }

    public String generateInviteTrainerNotification(String email, String link) {
        Context context = new Context();
        context.setVariable("otpLink", link);
        return templateEngine.process(configuration.getInviteTrainerNotification(), context);
    }
}
