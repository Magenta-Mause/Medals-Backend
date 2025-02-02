package com.medals.medalsbackend.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.security.config.JwtConfigurationProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtConfigurationProperties.class)
public class JwtUtils {

    private final JwtConfigurationProperties properties;
    private final ObjectMapper objectMapper;

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(properties.secretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @SneakyThrows
    public String generateToken(JwtTokenBody tokenBody) {
        Map<String, Object> claims = Map.of(
                "email", tokenBody.getEmail(),
                "tokenType", tokenBody.getTokenType(),
                "users", tokenBody.getAuthorizedUsers()
        );

        long tokenValidityDuration = tokenBody.getTokenType() == JwtTokenBody.TokenType.IDENTITY_TOKEN ?
                properties.identityTokenExpirationTime() :
                properties.refreshTokenExpirationTime();

        return Jwts.builder()
                .serializeToJsonWith(new JacksonSerializer<>(objectMapper))
                .setIssuedAt(new Date())
                .setSubject(tokenBody.getEmail())
                .setExpiration(new Date(new Date().getTime() + tokenValidityDuration))
                .addClaims(claims)
                .signWith(getSigningKey())
                .compact();
    }

    public long getRefreshTokenValidityDuration() {
        return properties.refreshTokenExpirationTime();
    }

    public long getIdentityTokenValidityDuration() {
        return properties.identityTokenExpirationTime();
    }
}
