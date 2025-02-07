package com.medals.medalsbackend.service.notifications.mail;

import org.springframework.stereotype.Service;

@Service
public interface MailService {
    void sendEmail(String receiver, String subject, String message);
}
