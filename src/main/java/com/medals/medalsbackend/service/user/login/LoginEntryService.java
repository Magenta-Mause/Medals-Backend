package com.medals.medalsbackend.service.user.login;

import com.medals.medalsbackend.entity.users.LoginEntry;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.exceptions.oneTimeCode.OneTimeCodeExpiredException;
import com.medals.medalsbackend.exceptions.oneTimeCode.OneTimeCodeNotFoundException;
import com.medals.medalsbackend.repository.LoginEntryRepository;
import com.medals.medalsbackend.service.user.login.jwt.JwtService;
import com.medals.medalsbackend.service.util.OneTimeCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginEntryService {
  private final LoginEntryRepository loginEntryRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final OneTimeCodeService oneTimeCodeService;

  public void createLoginEntry(String email) throws EmailAlreadyExistsException {
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

  public void setPassword(String oneTimeCode, String password) throws OneTimeCodeNotFoundException, OneTimeCodeExpiredException {
    String email = oneTimeCodeService.getEmailFromSetPasswordToken(oneTimeCode);
    LoginEntry loginEntry = loginEntryRepository.getReferenceById(email);
    loginEntry.setPassword(passwordEncoder.encode(password));
    loginEntryRepository.save(loginEntry);
    oneTimeCodeService.deleteOneTimeCode(oneTimeCode);
  }

  public Optional<LoginEntry> getByEmail(String email) {
    return loginEntryRepository.findById(email);
  }

  public LoginEntry checkLogin(String email, String password) throws EmailDoesntExistException, LoginDoesntMatchException {
    LoginEntry loginEntry = getByEmail(email).orElseThrow(() -> new EmailDoesntExistException(email));
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
    LoginEntry loginEntry = loginEntryRepository.getReferenceById(email);
    loginEntry.addUser(user);
    loginEntryRepository.save(loginEntry);
    return loginEntry.getUsers().getLast();
  }

}
