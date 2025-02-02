package com.medals.medalsbackend.service.user.login;

import com.medals.medalsbackend.entity.LoginEntry;
import com.medals.medalsbackend.security.jwt.JwtTokenBody;
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
                .authorizedUsers(loginEntry.getUsers())
                .email(loginEntry.getEmail())
                .build();

        return jwtUtils.generateToken(jwtTokenBody);
    }

}
