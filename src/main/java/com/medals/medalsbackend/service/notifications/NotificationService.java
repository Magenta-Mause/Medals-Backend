package com.medals.medalsbackend.service.notifications;

import com.medals.medalsbackend.service.notifications.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final MailService mailService;

  public void sendSetPasswordNotification(String email, String oneTimeCode) {
    String link = "http://localhost:5173/setPassword?oneTimeCode=" + oneTimeCode;
    mailService.sendEmail(email, "Medals - Account Creation", """
      <!DOCTYPE html>
      <html>
      <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>Account Creation</title>
          <style>
              body {
                  font-family: Arial, sans-serif;
                  background-color: #f4f4f4;
                  margin: 0;
                  padding: 20px;
              }
              .email-container {
                  max-width: 600px;
                  background: #ffffff;
                  padding: 20px;
                  border-radius: 8px;
                  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                  margin: auto;
              }
              .header {
                  font-size: 20px;
                  font-weight: bold;
                  color: #333;
                  padding-bottom: 10px;
              }
              h2 {
                  color: #333;
              }
              p {
                  color: #555;
                  line-height: 1.5;
              }
              .button {
                  display: inline-block;
                  padding: 10px 20px;
                  margin: 20px 0;
                  color: #fff;
                  background-color: #007bff;
                  text-decoration: none;
                  border-radius: 5px;
              }
              .footer {
                  font-size: 12px;
                  color: #888;
                  margin-top: 20px;
                  text-align: center;
                  border-top: 1px solid #ddd;
                  padding-top: 10px;
              }
              .footer a {
                  color: #555;
                  text-decoration: none;
              }
          </style>
      </head>
      <body>
      
      <div class="email-container">
          <div class="header">🥇 Medals</div>
         \s
          <p>+++ Deutsche Version unten +++</p>
          <hr>
         \s
          <h2>Account Creation</h2>
          <p>Hello,</p>
          <p>We have just created an account for you. Please set a password to access your account.</p>
          <p>Use this link to create your password:</p>
          <p><a class="button" href=\"""" + link + """
      ">Set Password</a></p>
      <p>If the button does not work, please use this link: \s
      <br><a href=\"""" + link + """
      ">""" + link + """
      </a></p>
      <p>If you did not request this or are not interested, you can simply ignore this email.</p>
      
      <hr>
      
      <h2>Kontoerstellung</h2>
      <p>Hallo,</p>
      <p>Wir haben soeben ein Konto für Sie erstellt. Bitte setzen Sie ein Passwort, um auf Ihr Konto zuzugreifen.</p>
      <p>Verwenden Sie diesen Link, um Ihr Passwort zu erstellen:</p>
      <p>
      <a class="button" href=\"""" + link + """
      ">Passwort festlegen</a>
      </p>
      <p>Falls die Schaltfläche nicht funktioniert, verwenden Sie bitte diesen Link: \s
      <br><a href=\"""" + link + """
      ">""" + link + """
          </a></p>
          <p>Falls Sie dies nicht angefordert haben oder nicht interessiert sind, können Sie diese E-Mail einfach ignorieren.</p>
      
          <div class="footer">
              <p>Medals Team</p>
          </div>
      </div>
      
      </body>
      </html>
      """
    );
  }
}
