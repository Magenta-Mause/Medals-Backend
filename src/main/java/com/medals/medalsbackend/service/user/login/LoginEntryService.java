package com.medals.medalsbackend.service.user.login;

import com.medals.medalsbackend.entity.onetimecode.OneTimeCodeType;
import com.medals.medalsbackend.entity.users.LoginEntry;
import com.medals.medalsbackend.entity.users.UserEntity;

import com.medals.medalsbackend.exception.onetimecode.OneTimeCodeExpiredException;
import com.medals.medalsbackend.exception.onetimecode.OneTimeCodeNotFoundException;
import com.medals.medalsbackend.repository.LoginEntryRepository;
import com.medals.medalsbackend.service.notifications.NotificationService;
import com.medals.medalsbackend.service.onetimecode.OneTimeCodeCreationReason;
import com.medals.medalsbackend.service.onetimecode.OneTimeCodeService;
import com.medals.medalsbackend.service.user.login.jwt.JwtService;
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

    public void createLoginEntry(String email, OneTimeCodeCreationReason reason) throws EmailAlreadyExistsException {
        if (loginEntryRepository.existsById(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        LoginEntry loginEntry = LoginEntry.builder()
                .email(email.toLowerCase())
                .password(null)
                .build();

        oneTimeCodeService.createSetPasswordToken(email, reason);
        loginEntryRepository.save(loginEntry);
    }

    public void initiateResetPasswordRequest(String email) {
        if (!loginEntryRepository.existsById(email.toLowerCase())) {
            return;
        }
        oneTimeCodeService.createResetPasswordToken(email);
    }

    public void setEntryPassword(String email, String newPassword) {
        LoginEntry loginEntry = loginEntryRepository.findById(email).get();
        loginEntry.setPassword(passwordEncoder.encode(newPassword));
        loginEntryRepository.save(loginEntry);
    }

    public void setPassword(String oneTimeCode, String password) throws OneTimeCodeNotFoundException, OneTimeCodeExpiredException {
        String email = oneTimeCodeService.getEmailFromOneTimeCode(oneTimeCode, OneTimeCodeType.SET_PASSWORD);
        setEntryPassword(email, password);
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
        LoginEntry loginEntry = loginEntryRepository.findById(email.toLowerCase()).orElseThrow(() -> new EmailDoesntExistException(email));
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
        LoginEntry loginEntry = loginEntryRepository.findById(email.toLowerCase()).orElseThrow(() -> new EmailDoesntExistException(email));
        loginEntry.addUser(user);
        loginEntryRepository.save(loginEntry);
        return loginEntry.getUsers().getLast();
    }

    public LoginEntry getLoginEntry(String userEmail) throws EmailDoesntExistException {
        return loginEntryRepository.findById(userEmail.toLowerCase()).orElseThrow(() -> new EmailDoesntExistException(userEmail));
    }
}
