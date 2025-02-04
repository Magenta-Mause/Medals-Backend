package com.medals.medalsbackend.service.util;

import com.medals.medalsbackend.entity.util.oneTimeCodes.OneTimeCode;
import com.medals.medalsbackend.entity.util.oneTimeCodes.OneTimeCodeType;
import com.medals.medalsbackend.exceptions.oneTimeCode.OneTimeCodeExpiredException;
import com.medals.medalsbackend.exceptions.oneTimeCode.OneTimeCodeNotFoundException;
import com.medals.medalsbackend.repository.OneTimeCodeRepository;
import com.medals.medalsbackend.service.notifications.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OneTimeCodeService {

  private final OneTimeCodeRepository oneTimeCodeRepository;
  private final NotificationService notificationService;

  public OneTimeCode createSetPasswordToken(String email) {
    OneTimeCode oneTimeCode;
    while (true) {
      try {
        oneTimeCode = OneTimeCode.builder()
          .authorizedEmail(email)
          .type(OneTimeCodeType.SET_PASSWORD)
          .oneTimeCode(UUID.randomUUID().toString())
          .build();
        oneTimeCodeRepository.save(oneTimeCode);
        break;
      } catch (Exception ignored) {
        log.warn("Exception occured while creating one time code: {}", ignored.getMessage());
      }
    }
    notificationService.sendSetPasswordNotification(email, oneTimeCode.oneTimeCode);
    return oneTimeCode;
  }

  public String getEmailFromSetPasswordToken(String setPasswordToken) throws OneTimeCodeNotFoundException, OneTimeCodeExpiredException {
    OneTimeCode oneTimeCode = oneTimeCodeRepository.findByOneTimeCode(setPasswordToken);
    if (oneTimeCode == null) {
      throw new OneTimeCodeNotFoundException();
    }
    if (oneTimeCode.expiresAt >= System.currentTimeMillis()) {
      throw new OneTimeCodeExpiredException();
    }
    return oneTimeCode.authorizedEmail;
  }

  @Transactional
  public void deleteOneTimeCode(String oneTimeCode) throws OneTimeCodeNotFoundException {
    if (oneTimeCode == null) {
      throw new OneTimeCodeNotFoundException();
    }
    if (oneTimeCodeRepository.findByOneTimeCode(oneTimeCode) == null) {
      throw new OneTimeCodeNotFoundException();
    }
    oneTimeCodeRepository.deleteAllByOneTimeCode(oneTimeCode);
  }
}
