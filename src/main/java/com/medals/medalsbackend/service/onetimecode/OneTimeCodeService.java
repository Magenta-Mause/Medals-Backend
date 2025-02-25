package com.medals.medalsbackend.service.onetimecode;

import com.medals.medalsbackend.config.OneTimeCodeConfiguration;
import com.medals.medalsbackend.entity.onetimecode.OneTimeCode;
import com.medals.medalsbackend.entity.onetimecode.OneTimeCodeType;
import com.medals.medalsbackend.exception.InternalException;
import com.medals.medalsbackend.exception.onetimecode.OneTimeCodeExpiredException;
import com.medals.medalsbackend.exception.onetimecode.OneTimeCodeNotFoundException;
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

    public OneTimeCode createSetPasswordToken(String email, OneTimeCodeCreationReason reason) {
        try {
            OneTimeCode oneTimeCode = generateOneTimeCode(OneTimeCodeType.SET_PASSWORD, email, oneTimeCodeConfiguration.setPasswordTokenValidityDuration());
            switch (reason) {
                case ACCOUNT_CREATED -> notificationService.sendCreateAccountNotification(email, oneTimeCode.oneTimeCode);
                case ACCOUNT_INVITED -> notificationService.sendInviteTrainerNotification(email, oneTimeCode.oneTimeCode);
            }
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

    public void createAthleteInviteToken(String email, String trainerName) {
        try {
            OneTimeCode oneTimeCode = generateOneTimeCode(OneTimeCodeType.VALIDATE_INVITE, email, oneTimeCodeConfiguration.validateInviteTokenDuration());
            notificationService.sendInviteAthleteNotification(email, oneTimeCode.oneTimeCode, trainerName);
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
