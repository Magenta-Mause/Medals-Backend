package com.medals.medalsbackend.service.user.login;

import com.medals.medalsbackend.entity.users.LoginEntry;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.entity.util.oneTimeCodes.OneTimeCodeType;
import com.medals.medalsbackend.exception.InternalException;
import com.medals.medalsbackend.exception.oneTimeCode.OneTimeCodeExpiredException;
import com.medals.medalsbackend.exception.oneTimeCode.OneTimeCodeNotFoundException;
import com.medals.medalsbackend.repository.LoginEntryRepository;
import com.medals.medalsbackend.service.notifications.NotificationService;
import com.medals.medalsbackend.service.user.login.jwt.JwtService;
import com.medals.medalsbackend.service.util.OneTimeCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginEntryService {
  private final LoginEntryRepository loginEntryRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final OneTimeCodeService oneTimeCodeService;
  private final NotificationService notificationService;

  public void createLoginEntry(String email) throws EmailAlreadyExistsException, InternalException {
    if (loginEntryRepository.existsById(email)) {
      throw new EmailAlreadyExistsException(email);
    }

    LoginEntry loginEntry = LoginEntry.builder()
      .email(email)
      .password(null)
      .build();

    oneTimeCodeService.createSetPasswordToken(email);
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
    LoginEntry loginEntry = loginEntryRepository.findById(email).get();
    loginEntry.setPassword(passwordEncoder.encode(password));
    loginEntryRepository.save(loginEntry);
    oneTimeCodeService.deleteOneTimeCode(oneTimeCode);
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
    if (!loginEntryRepository.existsById(email)) {
      throw new EmailDoesntExistException(email);
    }
    LoginEntry loginEntry = loginEntryRepository.findById(email).get();
    loginEntry.addUser(user);
    loginEntryRepository.save(loginEntry);
    return loginEntry.getUsers().getLast();
  }

  public LoginEntry getLoginEntry(String userEmail) throws EmailDoesntExistException {
    return loginEntryRepository.findById(userEmail).orElseThrow(() -> new EmailDoesntExistException(userEmail));
  }
}
