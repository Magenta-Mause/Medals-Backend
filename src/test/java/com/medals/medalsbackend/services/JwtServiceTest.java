package com.medals.medalsbackend.services;

import com.medals.medalsbackend.dto.authorization.TrainerAccessRequestDto;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class JwtServiceTest {

    @Mock
    private JwtUtils jwtUtils;
    @InjectMocks
    private JwtService jwtService;

    @Test
    public void testGenerateRefreshToken() {
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

        jwtService.buildRefreshToken(loginEntry);
        ArgumentCaptor<Map<String, Object>> jwtTokenBodyArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        verify(jwtUtils, times(1)).generateToken(jwtTokenBodyArgumentCaptor.capture());
        Map<String, Object> capturedClaims = jwtTokenBodyArgumentCaptor.getValue();

        assertEquals("test@gmail.com", capturedClaims.get("email"));
        assertNull(capturedClaims.get("aud"));
        assertEquals(JwtTokenBody.TokenType.REFRESH_TOKEN, capturedClaims.get("tokenType"));
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
        ArgumentCaptor<Map<String, Object>> jwtTokenBodyArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        verify(jwtUtils, times(1)).generateToken(jwtTokenBodyArgumentCaptor.capture());
        Map<String, Object> capturedTokenBody = jwtTokenBodyArgumentCaptor.getValue();

        assertEquals("test@gmail.com", capturedTokenBody.get("email"));
        assertEquals(loginEntry.getUsers(), capturedTokenBody.get("users"));
        assertEquals(JwtTokenBody.TokenType.IDENTITY_TOKEN, capturedTokenBody.get("tokenType"));
    }

    @Test
    public void testBuildInviteToken() {
        TrainerAccessRequestDto trainerAccessRequestDto = TrainerAccessRequestDto.builder()
                .athleteId((long) 2)
                .trainerId((long) 1)
                .build();

        String dummyToken = "dummyToken";
        when(jwtUtils.generateToken(anyMap())).thenReturn(dummyToken);

        jwtService.buildTrainerAccessRequestToken("test@gmail.com", trainerAccessRequestDto);
        ArgumentCaptor<Map<String, Object>> jwtTokenBodyArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        verify(jwtUtils, times(1)).generateToken(jwtTokenBodyArgumentCaptor.capture());
        Map<String, Object> capturedTokenBody = jwtTokenBodyArgumentCaptor.getValue();

        assertEquals("test@gmail.com", capturedTokenBody.get("email"));
        assertEquals(trainerAccessRequestDto.getTrainerId(), capturedTokenBody.get("trainerId"));
        assertEquals(trainerAccessRequestDto.getAthleteId(), capturedTokenBody.get("athleteId"));
        assertEquals(JwtTokenBody.TokenType.REQUEST_TOKEN, capturedTokenBody.get("tokenType"));
    }
}
