package com.medals.medalsbackend.service.notifications.mail.templates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

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

    public String loadTemplate(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        return Files.readString(Path.of(resource.getURI()), StandardCharsets.UTF_8);
    }

    public String generateSetPasswordNotification(String email, String link) throws IOException {
        return String.format(loadTemplate(configuration.getSetPasswordNotification()), link);
    }

    public String generateInviteTrainerNotification(String email, String link) throws IOException {
        return String.format(loadTemplate(configuration.getInviteTrainer()), link);
    }
}
