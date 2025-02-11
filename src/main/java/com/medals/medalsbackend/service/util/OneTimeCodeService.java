package com.medals.medalsbackend.service.util;

import com.medals.medalsbackend.entity.util.oneTimeCodes.OneTimeCode;
import com.medals.medalsbackend.entity.util.oneTimeCodes.OneTimeCodeType;
import com.medals.medalsbackend.exceptions.InternalException;
import com.medals.medalsbackend.exceptions.oneTimeCode.OneTimeCodeExpiredException;
import com.medals.medalsbackend.exceptions.oneTimeCode.OneTimeCodeNotFoundException;
import com.medals.medalsbackend.repository.OneTimeCodeRepository;
import com.medals.medalsbackend.service.notifications.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(OneTimeCodeConfiguration.class)
public class OneTimeCodeService {

    private final OneTimeCodeRepository oneTimeCodeRepository;
    private final NotificationService notificationService;
    private final OneTimeCodeConfiguration oneTimeCodeConfiguration;

    public OneTimeCode createSetPasswordToken(String email, OneTimeCodeCreationReason reason) throws InternalException {
        int tries = 20;
        while (tries > 0) {
            try {
                OneTimeCode oneTimeCode = OneTimeCode.builder()
                        .authorizedEmail(email)
                        .type(OneTimeCodeType.SET_PASSWORD)
                        .oneTimeCode(UUID.randomUUID().toString())
                        .expiresAt(System.currentTimeMillis() + oneTimeCodeConfiguration.setPasswordTokenValidityDuration())
                        .build();
                oneTimeCodeRepository.save(oneTimeCode);
                notificationService.sendSetPasswordNotification(email, oneTimeCode.oneTimeCode, reason);
                return oneTimeCode;
            } catch (Exception e) {
                log.warn("Exception occurred while creating one time code: {}", e.getMessage());
            }
            tries--;
        }
        throw new InternalException("Internal error while creating one time code");
    }

    public OneTimeCode createSetPasswordToken(String email) throws InternalException {
        return createSetPasswordToken(email, OneTimeCodeCreationReason.ACCOUNT_CREATED);
    }

    public String getEmailFromSetPasswordToken(String setPasswordToken) throws OneTimeCodeNotFoundException, OneTimeCodeExpiredException {
        OneTimeCode oneTimeCode = oneTimeCodeRepository.findByOneTimeCode(setPasswordToken);
        if (oneTimeCode == null) {
            throw new OneTimeCodeNotFoundException();
        }
        if (oneTimeCode.expiresAt < System.currentTimeMillis()) {
            throw new OneTimeCodeExpiredException();
        }
        if (oneTimeCode.type != OneTimeCodeType.SET_PASSWORD) {
            throw new OneTimeCodeNotFoundException();
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
