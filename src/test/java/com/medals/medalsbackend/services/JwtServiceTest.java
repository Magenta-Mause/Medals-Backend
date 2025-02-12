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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
        ArgumentCaptor<JwtTokenBody> jwtTokenBodyArgumentCaptor = ArgumentCaptor.forClass(JwtTokenBody.class);
        verify(jwtUtils, times(1)).generateToken(jwtTokenBodyArgumentCaptor.capture());
        assertEquals("test@gmail.com", jwtTokenBodyArgumentCaptor.getValue().getEmail());
        assertNull(jwtTokenBodyArgumentCaptor.getValue().getAuthorizedUsers());
        assertEquals(JwtTokenBody.TokenType.REFRESH_TOKEN, jwtTokenBodyArgumentCaptor.getValue().getTokenType());
    }

    @Test
    public void testGenerateIdentityToken() {
        jwtService.buildIdentityToken(
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
        ArgumentCaptor<JwtTokenBody> jwtTokenBodyArgumentCaptor = ArgumentCaptor.forClass(JwtTokenBody.class);
        verify(jwtUtils, times(1)).generateToken(jwtTokenBodyArgumentCaptor.capture());
        assertEquals("test@gmail.com", jwtTokenBodyArgumentCaptor.getValue().getEmail());
        assertEquals(JwtTokenBody.TokenType.IDENTITY_TOKEN, jwtTokenBodyArgumentCaptor.getValue().getTokenType());
        assertEquals("adminFirstName", jwtTokenBodyArgumentCaptor.getValue().getAuthorizedUsers().stream().findFirst().get().getFirstName());
        assertEquals("adminLastName", jwtTokenBodyArgumentCaptor.getValue().getAuthorizedUsers().stream().findFirst().get().getLastName());
        assertEquals("test@gmail.com", jwtTokenBodyArgumentCaptor.getValue().getAuthorizedUsers().stream().findFirst().get().getEmail());
    }
}
