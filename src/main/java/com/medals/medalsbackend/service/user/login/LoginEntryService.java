package com.medals.medalsbackend.service.user.login;

import com.medals.medalsbackend.entity.LoginEntry;
import com.medals.medalsbackend.entity.UserEntity;
import com.medals.medalsbackend.repository.LoginEntryRepository;
import com.medals.medalsbackend.service.user.login.jwt.JwtService;
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

    public void createLoginEntry(String email, String password) throws EmailAlreadyExistsException {
        if (loginEntryRepository.existsById(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        LoginEntry loginEntry = LoginEntry.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        loginEntryRepository.save(loginEntry);
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
