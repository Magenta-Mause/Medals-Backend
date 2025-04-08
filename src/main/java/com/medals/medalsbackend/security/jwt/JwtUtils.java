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

    public String generateToken(Map<String, Object> claims) {
        long tokenValidityDuration = switch ((JwtTokenBody.TokenType) claims.get("tokenType")) {
            case JwtTokenBody.TokenType.IDENTITY_TOKEN -> jwtConfigurationProperties.identityTokenExpirationTime();
            case JwtTokenBody.TokenType.REFRESH_TOKEN -> jwtConfigurationProperties.refreshTokenExpirationTime();
            case JwtTokenBody.TokenType.REQUEST_TOKEN -> jwtConfigurationProperties.athleteRequestTokenExpirationTime();
        };

        return Jwts.builder()
                .serializeToJsonWith(new JacksonSerializer<>(objectMapper))
                .setIssuedAt(new Date())
                .setAudience("medals-backend")
                .setSubject((String) claims.get("email"))
                .setExpiration(new Date(new Date().getTime() + tokenValidityDuration))
                .addClaims(claims)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Validates a JWT and extracts the subject claim of that token
     * @param token the JWT
     * @param tokenType the expected Token type
     * @return the subject claim of the token (in our case the email of the authorized user)
     * @throws JwtTokenInvalidException if JWT cant be validated or is a bad token
     */
    public String getJwtTokenUser(String token, JwtTokenBody.TokenType tokenType) throws JwtTokenInvalidException {
        return (String) getTokenContentBody(token, tokenType).get("sub");
    }

    public Map<String, Object> getTokenContentBody(String token, JwtTokenBody.TokenType tokenType) throws JwtTokenInvalidException {
        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            claims.getExpiration();
            if (!"medals-backend".equals(claims.get("aud"))) {
                throw new SecurityException("Missing/Bad audience claim");
            }
            if (!tokenType.toString().equals(claims.get("tokenType"))) {
                throw new SecurityException("Token type is not matching");
            }
            if (claims.get("sub") == null) {
                throw new SecurityException("Missing/Bad subject claim");
            }

            return claims;
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
