package com.medals.medalsbackend.service.user.login.jwt;

import com.medals.medalsbackend.dto.authorization.TrainerAccessRequestDto;
import com.medals.medalsbackend.entity.users.LoginEntry;
import com.medals.medalsbackend.entity.users.Trainer;
import com.medals.medalsbackend.security.jwt.JwtTokenBody;
import com.medals.medalsbackend.exception.JwtTokenInvalidException;
import com.medals.medalsbackend.security.jwt.JwtUtils;
import com.medals.medalsbackend.service.notifications.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtUtils jwtUtils;
    private final NotificationService notificationService;

    public String buildRefreshToken(LoginEntry loginEntry) {
        Map<String, Object> claims = Map.of(
                "email", loginEntry.getEmail(),
                "tokenType", JwtTokenBody.TokenType.REFRESH_TOKEN
        );

        return jwtUtils.generateToken(claims);
    }

    public String buildIdentityToken(LoginEntry loginEntry) {
        Map<String, Object> claims = Map.of(
                "email", loginEntry.getEmail(),
                "users", loginEntry.getUsers(),
                "tokenType", JwtTokenBody.TokenType.IDENTITY_TOKEN
        );

        return jwtUtils.generateToken(claims);
    }

    public void buildInviteToken(String athleteEmail, TrainerAccessRequestDto trainerAccessRequestDto, Trainer trainer) {
        Map<String, Object> claims = Map.of(
                "email", athleteEmail,
                "trainerId", trainerAccessRequestDto.getTrainerId(),
                "athleteId", trainerAccessRequestDto.getAthleteId(),
                "tokenType", JwtTokenBody.TokenType.REQUEST_TOKEN
        );
        String token = jwtUtils.generateToken(claims);
        notificationService.sendRequestAthleteNotification(athleteEmail, token, trainer);
    }

    public Map<String, Object> getTokenContentBody(String refreshToken, JwtTokenBody.TokenType tokenType) throws JwtTokenInvalidException {
        return jwtUtils.validateToken(refreshToken, tokenType);
    }
}
