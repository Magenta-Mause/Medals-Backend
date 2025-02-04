package com.medals.medalsbackend.service.user.login.jwt;

import com.medals.medalsbackend.entity.users.LoginEntry;
import com.medals.medalsbackend.security.jwt.JwtTokenBody;
import com.medals.medalsbackend.security.jwt.JwtTokenInvalidException;
import com.medals.medalsbackend.security.jwt.JwtUtils;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtUtils jwtUtils;

    public JwtService(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
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

    public String getUserEmailFromRefreshToken(String refreshToken) throws JwtTokenInvalidException {
        return jwtUtils.validateToken(refreshToken, JwtTokenBody.TokenType.REFRESH_TOKEN);
    }
}
