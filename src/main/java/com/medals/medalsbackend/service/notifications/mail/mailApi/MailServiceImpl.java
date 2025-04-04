package com.medals.medalsbackend.service.notifications.mail.mailApi;

import com.medals.medalsbackend.client.mail.MailClient;
import com.medals.medalsbackend.service.notifications.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Service
@EnableConfigurationProperties(MailServiceConfiguration.class)
public class MailServiceImpl implements MailService {
    private final MailServiceConfiguration configuration;
    private final MailClient mailClient;
    private final Environment environment;

    @Override
    public void sendEmail(String receiver, String subject, String message) {
        if (Arrays.stream(environment.getActiveProfiles()).toList().contains("test")) {
            return;
        }
        mailClient.sendMail(configuration.author(), receiver, message, subject);
    }
}
