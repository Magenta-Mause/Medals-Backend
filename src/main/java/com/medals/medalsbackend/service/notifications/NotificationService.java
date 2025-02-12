package com.medals.medalsbackend.service.notifications;

import com.medals.medalsbackend.service.notifications.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MailService mailService;

    public void sendPasswordResetNotification(String email) {
        mailService.sendEmail(email, "Password reset", """
                        <!DOCTYPE html>
                        <html>
                        <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Reseted Password</title>
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
                    font-size: 24px;
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
                    text-align: left;
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
                        <p>+++ Deutsche Version unten +++</p>
                        <hr>
                
                        <div class="header">🥇 Medals</div>
                           \s
                        <h2>Your password was reset</h2>
                        <p>Hello,</p>
                        <p>You just reset your password</p>
                
                        <p>If you did not reset your password, please contact an administrator and reset your password.</p>
                
                        <hr>
                
                        <h2>Passwort zurückgesetzt</h2>
                        <p>Hallo,</p>
                        <p>Du hast gerade dein Passwort zurückgesetzt</p>
                        <p>Falls sie ihr Passwort nicht geändert haben, kontaktieren Sie bitte einen Administrator und setzen sie ihr Passwort zurück.</p>
                        <div class="footer">
                        <p>Medals Team</p>
                        </div>
                        </div>
                
                        </body>
                        </html>
                """);
    }

    public void sendResetPasswordNotification(String email, String oneTimeCode) {
        String link = "http://localhost:5173/resetPassword?oneTimeCode=" + oneTimeCode;
        mailService.sendEmail(email, "Medals - Reset Password", """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Reset Your Password</title>
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
                            font-size: 24px;
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
                            text-align: left;
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
                    <p>+++ Deutsche Version unten +++</p>
                    <hr>
                
                    <div class="header">🥇 Medals</div>
                   \s
                    <h2>Reset Your Password</h2>
                    <p>Hello,</p>
                    <p>We received a request to reset your password. Click the button below to set a new password.</p>
                    <p><a class="button" href="{link}">Reset Password</a></p>
                  <p>If the button does not work, use this link: \s
                  <br><a href="{link}">{link}</a></p>
                  <p>If you did not request a password reset, you can safely ignore this email. Your password will remain unchanged.</p>
                
                  <hr>
                
                  <h2>Passwort zurücksetzen</h2>
                  <p>Hallo,</p>
                  <p>Wir haben eine Anfrage zum Zurücksetzen Ihres Passworts erhalten. Klicken Sie auf die Schaltfläche unten, um ein neues Passwort zu setzen.</p>
                  <p><a class="button" href="{link}">Passwort zurücksetzen</a></p>
                  <p>Falls die Schaltfläche nicht funktioniert, verwenden Sie bitte diesen Link: \s
                  <br><a href="{link}">{link}</a></p>
                    <p>Falls Sie kein neues Passwort anfordern wollten, können Sie diese E-Mail ignorieren. Ihr Passwort bleibt unverändert.</p>
                    <div class="footer">
                    <p>Medals Team</p>
                </div>
            </div>
           </body>
          </html>
          """.replaceAll("\\{link}", link)
        );
    }

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
                    <p><a class="button" href="{link}">Set Password</a></p>
                <p>If the button does not work, please use this link: \s
                <br><a href="{link}">{link}</a></p>
                <p>If you did not request this or are not interested, you can simply ignore this email.</p>
                
                <hr>
                
                <h2>Kontoerstellung</h2>
                <p>Hallo,</p>
                <p>Wir haben soeben ein Konto für Sie erstellt. Bitte setzen Sie ein Passwort, um auf Ihr Konto zuzugreifen.</p>
                <p>Verwenden Sie diesen Link, um Ihr Passwort zu erstellen:</p>
                <p>
                <a class="button" href="{link}">Passwort festlegen</a>
                </p>
                <p>Falls die Schaltfläche nicht funktioniert, verwenden Sie bitte diesen Link: \s
                <br><a href="{link}">{link}</a></p>
                    <p>Falls Sie dies nicht angefordert haben oder nicht interessiert sind, können Sie diese E-Mail einfach ignorieren.</p>
                
                    <div class="footer">
                        <p>Medals Team</p>
                    </div>
                </div>
                
                </body>
                </html>
                """.replaceAll("\\{link}", link)
        );
    }
}
