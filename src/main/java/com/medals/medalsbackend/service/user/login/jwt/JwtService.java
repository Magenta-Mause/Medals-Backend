package com.medals.medalsbackend.service.user.login.jwt;

import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.dto.authorization.AthleteSearchDto;
import com.medals.medalsbackend.entity.users.LoginEntry;
import com.medals.medalsbackend.security.jwt.JwtTokenBody;
import com.medals.medalsbackend.exception.JwtTokenInvalidException;
import com.medals.medalsbackend.security.jwt.JwtUtils;
import com.medals.medalsbackend.service.notifications.NotificationService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JwtService {

    private final JwtUtils jwtUtils;
    private final NotificationService notificationService;

    public JwtService(JwtUtils jwtUtils, NotificationService notificationService) {
        this.jwtUtils = jwtUtils;
        this.notificationService = notificationService;
    }

    public String buildRefreshToken(LoginEntry loginEntry) {
        JwtTokenBody jwtTokenBody = JwtTokenBody.builder()
                .tokenType(JwtTokenBody.TokenType.REFRESH_TOKEN)
                .email(loginEntry.getEmail())
                .build();

        Map<String, Object> claims = Map.of(
                "tokenType", JwtTokenBody.TokenType.REFRESH_TOKEN
        );

        return jwtUtils.generateToken(jwtTokenBody, claims);
    }

    public String buildIdentityToken(LoginEntry loginEntry) {
        JwtTokenBody jwtTokenBody = JwtTokenBody.builder()
                .tokenType(JwtTokenBody.TokenType.IDENTITY_TOKEN)
                .authorizedUsers(loginEntry.getUsers())
                .email(loginEntry.getEmail())
                .build();

        Map<String, Object> claims = Map.of(
                "tokenType", JwtTokenBody.TokenType.IDENTITY_TOKEN,
                "users", loginEntry.getUsers()
        );

        return jwtUtils.generateToken(jwtTokenBody, claims);
    }

    public void buildInviteToken(AthleteSearchDto athleteSearchDto, Long athleteId,  String trainerName) {
        JwtTokenBody jwtTokenBody = JwtTokenBody.builder()
                .tokenType(JwtTokenBody.TokenType.INVITE_TOKEN)
                .email(athleteSearchDto.getEmail())
                .build();

        Map<String, Object> claims = Map.of(
                "trainerId", athleteSearchDto.getTrainerId(),
                "athleteId", athleteId,
                "tokenType", JwtTokenBody.TokenType.INVITE_TOKEN
        );
        String token = jwtUtils.generateToken(jwtTokenBody, claims);
        notificationService.sendInviteAthleteNotification(athleteSearchDto.getEmail(), token, trainerName);
    }

    public Map<String, Object> getTokenContentBody(String refreshToken, JwtTokenBody.TokenType tokenType) throws JwtTokenInvalidException {
        return jwtUtils.validateToken(refreshToken, tokenType);
    }
}
