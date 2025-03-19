package com.medals.medalsbackend.service.user.login.jwt;

import com.medals.medalsbackend.dto.authorization.TrainerAccessRequestDto;
import com.medals.medalsbackend.entity.users.LoginEntry;
import com.medals.medalsbackend.security.jwt.JwtTokenBody;
import com.medals.medalsbackend.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtUtils jwtUtils;

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

    public String buildTrainerAccessRequestToken(String athleteEmail, TrainerAccessRequestDto trainerAccessRequestDto) {
        Map<String, Object> claims = Map.of(
                "email", athleteEmail,
                "trainerId", trainerAccessRequestDto.getTrainerId(),
                "athleteId", trainerAccessRequestDto.getAthleteId(),
                "tokenType", JwtTokenBody.TokenType.REQUEST_TOKEN
        );
        return jwtUtils.generateToken(claims);
    }
}
