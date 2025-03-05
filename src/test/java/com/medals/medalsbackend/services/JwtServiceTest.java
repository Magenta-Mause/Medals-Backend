package com.medals.medalsbackend.services;

import com.medals.medalsbackend.entity.users.Admin;
import com.medals.medalsbackend.entity.users.LoginEntry;
import com.medals.medalsbackend.security.jwt.JwtTokenBody;
import com.medals.medalsbackend.security.jwt.JwtUtils;
import com.medals.medalsbackend.service.user.login.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class JwtServiceTest {

    @Mock
    private JwtUtils jwtUtils;
    @InjectMocks
    private JwtService jwtService;

    @Test
    public void testGenerateRefreshToken() {
        jwtService.buildRefreshToken(
                LoginEntry.builder()
                        .email("test@gmail.com")
                        .users(List.of(
                                Admin.builder()
                                        .email("test@gmail.com")
                                        .lastName("adminLastName")
                                        .firstName("adminFirstName")
                                        .build()
                        ))
                        .password("password")
                        .build()
        );

        Map<String, Object> claims = Map.of(
                "tokenType", JwtTokenBody.TokenType.REFRESH_TOKEN
        );
        ArgumentCaptor<JwtTokenBody> jwtTokenBodyArgumentCaptor = ArgumentCaptor.forClass(JwtTokenBody.class);
        verify(jwtUtils, times(1)).generateToken(jwtTokenBodyArgumentCaptor.capture(), eq(claims));
        JwtTokenBody capturedTokenBody = jwtTokenBodyArgumentCaptor.getValue();

        assertEquals("test@gmail.com", capturedTokenBody.getEmail());
        assertNull(capturedTokenBody.getAuthorizedUsers());
        assertEquals(JwtTokenBody.TokenType.REFRESH_TOKEN, capturedTokenBody.getTokenType());
    }

    @Test
    public void testBuildIdentityToken() {
        LoginEntry loginEntry = LoginEntry.builder()
                .email("test@gmail.com")
                .users(List.of(
                        Admin.builder()
                                .email("test@gmail.com")
                                .lastName("adminLastName")
                                .firstName("adminFirstName")
                                .build()
                ))
                .build();

        jwtService.buildIdentityToken(loginEntry);

        Map<String, Object> claims = Map.of(
                "tokenType", JwtTokenBody.TokenType.IDENTITY_TOKEN,
                "users", loginEntry.getUsers()
        );

        ArgumentCaptor<JwtTokenBody> jwtTokenBodyArgumentCaptor = ArgumentCaptor.forClass(JwtTokenBody.class);
        verify(jwtUtils, times(1)).generateToken(jwtTokenBodyArgumentCaptor.capture(), eq(claims));

        JwtTokenBody capturedTokenBody = jwtTokenBodyArgumentCaptor.getValue();

        assertEquals("test@gmail.com", capturedTokenBody.getEmail());
        assertEquals(loginEntry.getUsers(), capturedTokenBody.getAuthorizedUsers());
        assertEquals(JwtTokenBody.TokenType.IDENTITY_TOKEN, capturedTokenBody.getTokenType());
    }
}
