package com.medals.medalsbackend;

import com.medals.medalsbackend.entity.util.oneTimeCodes.OneTimeCode;
import com.medals.medalsbackend.entity.util.oneTimeCodes.OneTimeCodeType;
import com.medals.medalsbackend.exceptions.oneTimeCode.OneTimeCodeExpiredException;
import com.medals.medalsbackend.exceptions.oneTimeCode.OneTimeCodeNotFoundException;
import com.medals.medalsbackend.repository.OneTimeCodeRepository;
import com.medals.medalsbackend.service.notifications.NotificationService;
import com.medals.medalsbackend.service.util.OneTimeCodeConfiguration;
import com.medals.medalsbackend.service.util.OneTimeCodeService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OneTimeCodeServiceTest {
    @Mock
    private OneTimeCodeRepository oneTimeCodeRepository;
    @Mock
    private OneTimeCodeConfiguration oneTimeCodeConfiguration;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private OneTimeCodeService oneTimeCodeService;

    @Test
    @SneakyThrows
    public void testSetPasswordToken() {
        when(oneTimeCodeConfiguration.setPasswordTokenValidityDuration()).thenReturn(100L);
        oneTimeCodeService.createSetPasswordToken("email");

        ArgumentCaptor<OneTimeCode> oneTimeCodeArgumentCaptor = ArgumentCaptor.forClass(OneTimeCode.class);
        verify(oneTimeCodeRepository, times(1)).save(oneTimeCodeArgumentCaptor.capture());
        verify(notificationService, times(1)).sendSetPasswordNotification(eq("email"), any());
        assertEquals("email", oneTimeCodeArgumentCaptor.getValue().authorizedEmail);
        assertEquals(OneTimeCodeType.SET_PASSWORD, oneTimeCodeArgumentCaptor.getValue().type);
        assertTrue(System.currentTimeMillis() <= oneTimeCodeArgumentCaptor.getValue().expiresAt && oneTimeCodeArgumentCaptor.getValue().expiresAt <= System.currentTimeMillis() + 100L);
    }

    @SneakyThrows
    @Test
    public void testOneTimeCodeValidation() {
        when(oneTimeCodeRepository.findByOneTimeCode(eq("testcode"))).thenReturn(OneTimeCode.builder().type(OneTimeCodeType.SET_PASSWORD).authorizedEmail("email").expiresAt(System.currentTimeMillis() + 500000).build());
        String email = oneTimeCodeService.getEmailFromSetPasswordToken("testcode");
        assertEquals("email", email);
    }

    @Test
    @SneakyThrows
    public void testOneTimeCodeExpired() {
        when(oneTimeCodeRepository.findByOneTimeCode(eq("testcode"))).thenReturn(OneTimeCode.builder().type(OneTimeCodeType.SET_PASSWORD).authorizedEmail("email").expiresAt(System.currentTimeMillis() - 10000).build());
        assertThrows(OneTimeCodeExpiredException.class, () -> oneTimeCodeService.getEmailFromSetPasswordToken("testcode"));
    }

    @Test
    @SneakyThrows
    public void testOneTimeCodeNotFound() {
        when(oneTimeCodeRepository.findByOneTimeCode(eq("testcode"))).thenReturn(null);
        assertThrows(OneTimeCodeNotFoundException.class, () -> oneTimeCodeService.getEmailFromSetPasswordToken("testcode"));
    }


    @Test
    @SneakyThrows
    public void testOneTimeCodeTypeNotMatching() {
        when(oneTimeCodeRepository.findByOneTimeCode(eq("testcode"))).thenReturn(OneTimeCode.builder().type(null).expiresAt(System.currentTimeMillis() + 10000).build());
        assertThrows(OneTimeCodeNotFoundException.class, () -> oneTimeCodeService.getEmailFromSetPasswordToken("testcode"));
    }
}
