package com.medals.medalsbackend.service.notifications;

import com.medals.medalsbackend.service.notifications.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final MailService mailService;

  public void sendSetPasswordNotification(String email, String oneTimeCode) {
    mailService.sendEmail(email, "Medals - Account Creation", """
      <h1>Medals</h1>
      <p>
      Hello dear user,
      we just created an Account for you.
      Create a password to access your account.
      Use this Link to create your account: http://localhost:5173/setPassword?oneTimeCode=""" + oneTimeCode + """
      </p>
      """
    );
  }
}
