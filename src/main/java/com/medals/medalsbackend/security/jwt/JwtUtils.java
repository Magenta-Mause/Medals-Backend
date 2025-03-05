package com.medals.medalsbackend.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.exception.JwtTokenInvalidException;
import com.medals.medalsbackend.security.config.JwtConfigurationProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtConfigurationProperties.class)
public class JwtUtils {

    private final JwtParser jwtParser;
    private final JwtConfigurationProperties jwtConfigurationProperties;
    private final Key signingKey;
    private final ObjectMapper objectMapper;

    public String generateToken(JwtTokenBody tokenBody, Map<String, Object> claims) {
        long tokenValidityDuration = switch (tokenBody.getTokenType()) {
            case JwtTokenBody.TokenType.IDENTITY_TOKEN -> jwtConfigurationProperties.identityTokenExpirationTime();
            case JwtTokenBody.TokenType.REFRESH_TOKEN -> jwtConfigurationProperties.refreshTokenExpirationTime();
            case JwtTokenBody.TokenType.INVITE_TOKEN -> jwtConfigurationProperties.athleteInviteTokenExpirationTime();
        };

        return Jwts.builder()
                .serializeToJsonWith(new JacksonSerializer<>(objectMapper))
                .setIssuedAt(new Date())
                .setAudience("medals-backend")
                .setSubject(tokenBody.getEmail())
                .setExpiration(new Date(new Date().getTime() + tokenValidityDuration))
                .addClaims(claims)
                .signWith(signingKey)
                .compact();
    }

    public Map<String, Object> validateToken(String token, JwtTokenBody.TokenType tokenType) throws JwtTokenInvalidException {
        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            claims.getExpiration();
            if (!"medals-backend".equals(claims.getAudience())) {
                throw new SecurityException("Missing/Bad audience claim");
            }
            if (!tokenType.toString().equals(claims.get("tokenType"))) {
                throw new SecurityException("Token type is not matching");
            }
            if (claims.getSubject() == null) {
                throw new SecurityException("Missing/Bad subject claim");
            }

            return new HashMap<>(claims);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SecurityException | AssertionError e) {
            log.error("Error validating token", e);
            throw new JwtTokenInvalidException();
        } catch (Exception e) {
            log.error("Error while parsing JWT token", e);
            throw new JwtTokenInvalidException();
        }
    }

    public long getRefreshTokenValidityDuration() {
        return jwtConfigurationProperties.refreshTokenExpirationTime();
    }

    public long getIdentityTokenValidityDuration() {
        return jwtConfigurationProperties.identityTokenExpirationTime();
    }
}
