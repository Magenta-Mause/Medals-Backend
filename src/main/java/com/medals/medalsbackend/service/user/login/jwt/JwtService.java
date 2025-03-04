package com.medals.medalsbackend.service.user.login.jwt;

import com.medals.medalsbackend.dto.AthleteDto;
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

        return jwtUtils.generateToken(jwtTokenBody);
    }

    public String buildIdentityToken(LoginEntry loginEntry) {
        JwtTokenBody jwtTokenBody = JwtTokenBody.builder()
                .tokenType(JwtTokenBody.TokenType.IDENTITY_TOKEN)
                .authorizedUsers(loginEntry.getUsers())
                .email(loginEntry.getEmail())
                .build();

        return jwtUtils.generateToken(jwtTokenBody);
    }

    public void buildInviteToken(AthleteDto athleteDto, long trainerId, String trainerName) {
        JwtTokenBody jwtTokenBody = JwtTokenBody.builder()
                .tokenType(JwtTokenBody.TokenType.INVITE_TOKEN)
                .email(athleteDto.getEmail())
                .build();

        Map<String, Object> claims = Map.of(
                "trainerId", trainerId,
                "athleteId", athleteDto.getId(),
                "tokenType", JwtTokenBody.TokenType.INVITE_TOKEN
        );
        String token = jwtUtils.buildInviteToken(jwtTokenBody, claims);
        notificationService.sendInviteAthleteNotification(athleteDto.getEmail(), token, trainerName);
    }

    public String getUserEmailFromRefreshToken(String refreshToken) throws JwtTokenInvalidException {
        return jwtUtils.validateToken(refreshToken, JwtTokenBody.TokenType.REFRESH_TOKEN);
    }

    public String getSearchTerm(String token, String searchTerm) throws JwtTokenInvalidException {
        return jwtUtils.getInfoInToken(token, JwtTokenBody.TokenType.INVITE_TOKEN, searchTerm);
    }
}
