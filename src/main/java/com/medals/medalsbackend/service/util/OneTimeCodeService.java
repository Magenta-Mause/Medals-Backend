package com.medals.medalsbackend.service.util;

import com.medals.medalsbackend.entity.util.oneTimeCodes.OneTimeCode;
import com.medals.medalsbackend.entity.util.oneTimeCodes.OneTimeCodeType;
import com.medals.medalsbackend.exception.InternalException;
import com.medals.medalsbackend.exception.oneTimeCode.OneTimeCodeExpiredException;
import com.medals.medalsbackend.exception.oneTimeCode.OneTimeCodeNotFoundException;
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

    public OneTimeCode generateOneTimeCode(OneTimeCodeType type, String email, long validityDuration) throws InternalException {
        int tries = 20;
        while (tries > 0) {
            try {
                OneTimeCode oneTimeCode = OneTimeCode.builder()
                        .oneTimeCode(UUID.randomUUID().toString()).authorizedEmail(email)
                        .type(type)
                        .expiresAt(System.currentTimeMillis() + validityDuration)
                        .build();
                oneTimeCodeRepository.save(oneTimeCode);
                return oneTimeCode;
            } catch (Exception e) {
                log.warn("Generating one time code failed; {} Tries remaining", tries, e);
            }
            tries--;
        }
        throw new InternalException("Couldnt generate one time code");
    }

    public OneTimeCode createSetPasswordToken(String email) {
        try {
            OneTimeCode oneTimeCode = generateOneTimeCode(OneTimeCodeType.SET_PASSWORD, email, oneTimeCodeConfiguration.setPasswordTokenValidityDuration());
            notificationService.sendSetPasswordNotification(email, oneTimeCode.oneTimeCode);
            return oneTimeCode;
        } catch (InternalException e) {
            throw new RuntimeException(e);
        }
    }

    public OneTimeCode createResetPasswordToken(String email) {
        try {
            OneTimeCode oneTimeCode = generateOneTimeCode(OneTimeCodeType.RESET_PASSWORD, email, oneTimeCodeConfiguration.resetPasswordTokenValidityDuration());
            notificationService.sendResetPasswordNotification(email, oneTimeCode.oneTimeCode);
            return oneTimeCode;
        } catch (InternalException e) {
            throw new RuntimeException(e);
        }
    }

    public String getEmailFromOneTimeCode(String incomingOneTimeCode, OneTimeCodeType oneTimeCodeType) throws OneTimeCodeNotFoundException, OneTimeCodeExpiredException {
        OneTimeCode oneTimeCode = oneTimeCodeRepository.findByOneTimeCode(incomingOneTimeCode);
        if (oneTimeCode == null) {
            throw new OneTimeCodeNotFoundException();
        }
        if (oneTimeCode.expiresAt < System.currentTimeMillis()) {
            throw new OneTimeCodeExpiredException();
        }
        if (oneTimeCode.type != oneTimeCodeType) {
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
