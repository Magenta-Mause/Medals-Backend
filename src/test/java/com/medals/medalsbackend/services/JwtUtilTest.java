package com.medals.medalsbackend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.medals.medalsbackend.entity.users.Admin;
import com.medals.medalsbackend.exception.JwtTokenInvalidException;
import com.medals.medalsbackend.security.config.JwtConfigurationProperties;
import com.medals.medalsbackend.security.jwt.JwtTokenBody;
import com.medals.medalsbackend.security.jwt.JwtUtils;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private final String testEmail = "test@example.com";
    private final long testExpirationTime = 60000L;
    @Mock
    private JwtParser jwtParser;
    @Mock
    private JwtConfigurationProperties jwtConfigurationProperties;
    @Mock
    private Key signingKey;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        byte[] secretKey = Base64.getDecoder().decode("2bdff806c775df3d3a9a720924294d3f6dfa4e4e8f65183294bc8b8f043987ac");
        signingKey = Keys.hmacShaKeyFor(secretKey);

        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        jwtConfigurationProperties = new JwtConfigurationProperties("secretKey", testExpirationTime, testExpirationTime, testExpirationTime);
        jwtParser = Jwts.parserBuilder().setSigningKey(signingKey).build();
        jwtUtils = new JwtUtils(jwtParser, jwtConfigurationProperties, signingKey, objectMapper);
    }

    @SneakyThrows
    @Test
    void testGenerateIdentityJwtToken() {
        JwtTokenBody tokenBody = JwtTokenBody.builder()
                .email(testEmail)
                .tokenType(JwtTokenBody.TokenType.IDENTITY_TOKEN)
                .authorizedUsers(List.of(
                        Admin.builder().firstName("adminFirstName").lastName("adminLastName").email("admin@email.com").id(1L).build()
                ))
                .build();
        String token = jwtUtils.generateToken(tokenBody);
        jwtUtils.validateToken(token, JwtTokenBody.TokenType.IDENTITY_TOKEN);
    }

    @Test
    void testGenerateRefreshToken() throws JwtTokenInvalidException {
        JwtTokenBody tokenBody = JwtTokenBody.builder()
                .email(testEmail)
                .tokenType(JwtTokenBody.TokenType.REFRESH_TOKEN)
                .build();
        String token = jwtUtils.generateToken(tokenBody);
        jwtUtils.validateToken(token, JwtTokenBody.TokenType.REFRESH_TOKEN);
    }

    @Test
    void testInvalidTokenException() {
        assertThrows(JwtTokenInvalidException.class, () -> {
            jwtUtils.validateToken("testtoken.token.secret", JwtTokenBody.TokenType.IDENTITY_TOKEN);
        });
    }

    @Test
    void testExpiredToken() {
        Map<String, Object> claims = Map.of(
                "tokenType", JwtTokenBody.TokenType.IDENTITY_TOKEN
        );
        String token = Jwts.builder()
                .serializeToJsonWith(new JacksonSerializer<>(objectMapper))
                .setExpiration(new Date(new Date().getTime() - 1000))
                .addClaims(claims)
                .signWith(signingKey)
                .compact();

        assertThrows(JwtTokenInvalidException.class, () -> {
            jwtUtils.validateToken(token, JwtTokenBody.TokenType.IDENTITY_TOKEN);
        });
    }

    @Test
    void testTokenWithMissingAudience() {
        Map<String, Object> claims = Map.of(
                "tokenType", JwtTokenBody.TokenType.IDENTITY_TOKEN
        );
        String token = Jwts.builder()
                .serializeToJsonWith(new JacksonSerializer<>(objectMapper))
                .setExpiration(new Date(new Date().getTime() + 1000))
                .addClaims(claims)
                .signWith(signingKey)
                .compact();

        assertThrows(JwtTokenInvalidException.class, () -> {
            jwtUtils.validateToken(token, JwtTokenBody.TokenType.IDENTITY_TOKEN);
        });
    }

    @SneakyThrows
    @Test
    void testValidJwtToken() {
        Map<String, Object> claims = Map.of(
                "tokenType", JwtTokenBody.TokenType.REFRESH_TOKEN
        );

        String token = Jwts.builder()
                .serializeToJsonWith(new JacksonSerializer<>(objectMapper))
                .setExpiration(new Date(new Date().getTime() + 1000))
                .addClaims(claims)
                .setAudience("medals-backend")
                .setSubject(testEmail)
                .signWith(signingKey)
                .compact();

        String user = jwtUtils.validateToken(token, JwtTokenBody.TokenType.REFRESH_TOKEN);
        assertEquals(user, testEmail);
    }

    @SneakyThrows
    @Test
    void testJwtTokenWithMissingSubject() {
        Map<String, Object> claims = Map.of(
                "tokenType", JwtTokenBody.TokenType.REFRESH_TOKEN
        );

        String token = Jwts.builder()
                .serializeToJsonWith(new JacksonSerializer<>(objectMapper))
                .setExpiration(new Date(new Date().getTime() + 1000))
                .addClaims(claims)
                .setAudience("medals-backend")
                .signWith(signingKey)
                .compact();

        assertThrows(JwtTokenInvalidException.class, () -> jwtUtils.validateToken(token, JwtTokenBody.TokenType.REFRESH_TOKEN));
    }

    @SneakyThrows
    @Test
    void testJwtTokenWithInvalidTokenType() {
        Map<String, Object> claims = Map.of(
                "tokenType", JwtTokenBody.TokenType.REFRESH_TOKEN
        );

        String token = Jwts.builder()
                .serializeToJsonWith(new JacksonSerializer<>(objectMapper))
                .setExpiration(new Date(new Date().getTime() + 1000))
                .addClaims(claims)
                .setAudience("medals-backend")
                .signWith(signingKey)
                .compact();

        assertThrows(JwtTokenInvalidException.class, () -> jwtUtils.validateToken(token, JwtTokenBody.TokenType.IDENTITY_TOKEN));
    }
}
