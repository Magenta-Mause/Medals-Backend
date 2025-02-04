package com.medals.medalsbackend.service.notifications.mail.mailApi;

import com.medals.medalsbackend.client.mail.MailClient;
import com.medals.medalsbackend.service.notifications.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@EnableConfigurationProperties(MailServiceConfiguration.class)
public class MailServiceImpl implements MailService {
    private final MailServiceConfiguration configuration;
    private final MailClient mailClient;

    @Override
    public void sendEmail(String receiver, String subject, String message) {
        mailClient.sendMail(configuration.author(), receiver, message, subject);
    }
}
