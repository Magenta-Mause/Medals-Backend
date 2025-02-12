package com.medals.medalsbackend.service.notifications;

import com.medals.medalsbackend.service.notifications.mail.MailService;
import com.medals.medalsbackend.service.notifications.mail.templates.MailTemplateService;
import com.medals.medalsbackend.service.util.OneTimeCodeCreationReason;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.pqc.crypto.ExchangePair;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MailService mailService;
    private final MailTemplateService templateService;

    public void sendSetPasswordNotification(String email, String oneTimeCode, OneTimeCodeCreationReason reason) {
      switch (reason) {
        case ACCOUNT_CREATED -> sendCreateAccountNotification(email, oneTimeCode);
        case ACCOUNT_INVITED -> sendInviteTrainerNotification(email, oneTimeCode);
        default -> log.warn("Invalid OneTimeCodeCreationReason supplied: " + reason);
      }
    }

  public void sendSetPasswordNotification(String email, String oneTimeCode) {
    sendSetPasswordNotification(email, oneTimeCode, OneTimeCodeCreationReason.ACCOUNT_CREATED);
  }

    public void sendCreateAccountNotification(String email, String oneTimeCode) {
      String link = "http://localhost:5173/setPassword?oneTimeCode=" + oneTimeCode;
        String text = templateService.generateSetPasswordNotification(email, link);
        mailService.sendEmail(email, "Medals - Account Creation", text);
    }

    public void sendInviteTrainerNotification(String email, String oneTimeCode) {

        String link = "http://localhost:5173/setPassword?oneTimeCode=" + oneTimeCode;
        String text = templateService.generateInviteTrainerNotification(email, link);
        mailService.sendEmail(email, "Medals - Trainer Creation", text);
    }
}
