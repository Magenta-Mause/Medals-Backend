package com.medals.medalsbackend.service.user.login;

import com.medals.medalsbackend.entity.users.LoginEntry;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.entity.onetimecode.OneTimeCodeType;
import com.medals.medalsbackend.exception.InternalException;
import com.medals.medalsbackend.exception.onetimecode.OneTimeCodeExpiredException;
import com.medals.medalsbackend.exception.onetimecode.OneTimeCodeNotFoundException;
import com.medals.medalsbackend.repository.LoginEntryRepository;
import com.medals.medalsbackend.service.notifications.NotificationService;
import com.medals.medalsbackend.service.user.login.jwt.JwtService;
import com.medals.medalsbackend.service.onetimecode.OneTimeCodeCreationReason;
import com.medals.medalsbackend.service.onetimecode.OneTimeCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginEntryService {
  private final LoginEntryRepository loginEntryRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final OneTimeCodeService oneTimeCodeService;
  private final NotificationService notificationService;

  public void createLoginEntry(String email, OneTimeCodeCreationReason reason) throws EmailAlreadyExistsException, InternalException {
    if (loginEntryRepository.existsById(email)) {
      throw new EmailAlreadyExistsException(email);
    }

    LoginEntry loginEntry = LoginEntry.builder()
      .email(email)
      .password(null)
      .build();

    oneTimeCodeService.createSetPasswordToken(email, reason);
    loginEntryRepository.save(loginEntry);
  }

  public void initiateResetPasswordRequest(String email) throws EmailDoesntExistException {
    if (!loginEntryRepository.existsById(email)) {
      throw new EmailDoesntExistException(email);
    }
    oneTimeCodeService.createResetPasswordToken(email);
  }

  public void setPassword(String oneTimeCode, String password) throws OneTimeCodeNotFoundException, OneTimeCodeExpiredException {
    String email = oneTimeCodeService.getEmailFromOneTimeCode(oneTimeCode, OneTimeCodeType.SET_PASSWORD);
    log.info("Got Email for oneTimeCode: {} {}", oneTimeCode, email);
    LoginEntry loginEntry = loginEntryRepository.findById(email).get();
    log.info("Found loginEntry for oneTimeCode: {}", oneTimeCode);
    loginEntry.setPassword(passwordEncoder.encode(password));
    log.info("Setting password for one time code: {}", oneTimeCode);
    loginEntryRepository.save(loginEntry);
    log.info("Savin loginEntry in DB for oneTimeCode: {}", oneTimeCode);
    oneTimeCodeService.deleteOneTimeCode(oneTimeCode);
    log.info("Removing oneTimeCode from DB for oneTimeCode: {}", oneTimeCode);
  }

  public void resetPassword(String newPassword, String oneTimeCode) throws OneTimeCodeNotFoundException, OneTimeCodeExpiredException {
    String userEmail = oneTimeCodeService.getEmailFromOneTimeCode(oneTimeCode, OneTimeCodeType.RESET_PASSWORD);
    LoginEntry loginEntry = loginEntryRepository.findById(userEmail).get();
    loginEntry.setPassword(passwordEncoder.encode(newPassword));
    loginEntryRepository.save(loginEntry);
    oneTimeCodeService.deleteOneTimeCode(oneTimeCode);
    notificationService.sendPasswordResetNotification(userEmail);
  }

  public LoginEntry checkLogin(String email, String password) throws EmailDoesntExistException, LoginDoesntMatchException {
    LoginEntry loginEntry = loginEntryRepository.findById(email).orElseThrow(() -> new EmailDoesntExistException(email));
    if (!passwordEncoder.matches(password, loginEntry.getPassword())) {
      throw new LoginDoesntMatchException(null);
    }
    return loginEntry;
  }

  public String buildJwtRefreshToken(String email, String password) throws EmailDoesntExistException, LoginDoesntMatchException {
    LoginEntry loginEntry = checkLogin(email, password);
    return jwtService.buildRefreshToken(loginEntry);
  }

  public UserEntity addUserToLogin(String email, UserEntity user) throws EmailDoesntExistException {
    LoginEntry loginEntry = loginEntryRepository.findById(email).orElseThrow(() -> new EmailDoesntExistException(email));
    loginEntry.addUser(user);
    loginEntryRepository.save(loginEntry);
    return loginEntry.getUsers().getLast();
  }

  public LoginEntry getLoginEntry(String userEmail) throws EmailDoesntExistException {
    return loginEntryRepository.findById(userEmail).orElseThrow(() -> new EmailDoesntExistException(userEmail));
  }
}
