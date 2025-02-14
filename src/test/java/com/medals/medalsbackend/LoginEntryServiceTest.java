package com.medals.medalsbackend;

import com.medals.medalsbackend.entity.users.Admin;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.users.LoginEntry;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.entity.util.oneTimeCodes.OneTimeCodeType;
import com.medals.medalsbackend.exception.oneTimeCode.OneTimeCodeExpiredException;
import com.medals.medalsbackend.repository.LoginEntryRepository;
import com.medals.medalsbackend.service.user.login.EmailDoesntExistException;
import com.medals.medalsbackend.service.user.login.LoginDoesntMatchException;
import com.medals.medalsbackend.service.user.login.LoginEntryService;
import com.medals.medalsbackend.service.user.login.jwt.JwtService;
import com.medals.medalsbackend.service.util.OneTimeCodeCreationReason;
import com.medals.medalsbackend.service.util.OneTimeCodeService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class LoginEntryServiceTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private LoginEntryRepository loginEntryRepository;
    @Mock
    private OneTimeCodeService oneTimeCodeService;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @InjectMocks
    private LoginEntryService loginEntryService;

    @SneakyThrows
    @Test
    public void testLoginEntryCreationTriggersEmailSending() {
        when(loginEntryRepository.existsById(any())).thenReturn(false);
        loginEntryService.createLoginEntry("test@gmail.com");
        verify(oneTimeCodeService, times(1)).createSetPasswordToken(eq("test@gmail.com"), eq(OneTimeCodeCreationReason.ACCOUNT_CREATED));
    }

    @Test
    @SneakyThrows
    public void testSetPasswordTokenValidity() {
        when(oneTimeCodeService.getEmailFromOneTimeCode(any(), any())).thenReturn("test@gmail.com");
        when(loginEntryRepository.findById("test@gmail.com")).thenReturn(Optional.of(LoginEntry.builder().email("test@gmail.com").build()));
        when(bCryptPasswordEncoder.encode(any())).thenReturn("encryptedPassword");

        ArgumentCaptor<LoginEntry> loginEntryArgumentCaptor = ArgumentCaptor.forClass(LoginEntry.class);

        loginEntryService.setPassword("test", "newPassword");
        verify(loginEntryRepository, times(1)).findById("test@gmail.com");
        verify(loginEntryRepository, times(1)).save(loginEntryArgumentCaptor.capture());

        assertEquals("test@gmail.com", loginEntryArgumentCaptor.getValue().getEmail());
        assertEquals("encryptedPassword", loginEntryArgumentCaptor.getValue().getPassword());
    }


    @Test
    @SneakyThrows
    public void testCheckLogin() {
        when(loginEntryRepository.findById(eq("test@gmail.com"))).thenReturn(Optional.of(LoginEntry.builder().password("encryptedPassword").email("test@gmail.com").build()));
        when(bCryptPasswordEncoder.matches(eq("testPassword"), eq("encryptedPassword"))).thenReturn(true);

        LoginEntry result = loginEntryService.checkLogin("test@gmail.com", "testPassword");
        assertEquals("test@gmail.com", result.getEmail());
    }

    @Test
    @SneakyThrows
    public void testBuildJwtRefreshToken() {
        when(loginEntryRepository.findById(eq("test@gmail.com"))).thenReturn(Optional.of(LoginEntry.builder().password("encryptedPassword").email("test@gmail.com").build()));
        when(bCryptPasswordEncoder.matches(eq("testPassword"), eq("encryptedPassword"))).thenReturn(true);
        loginEntryService.buildJwtRefreshToken("test@gmail.com", "testPassword");

        ArgumentCaptor<LoginEntry> loginEntryArgumentCaptor = ArgumentCaptor.forClass(LoginEntry.class);
        verify(jwtService, times(1)).buildRefreshToken(loginEntryArgumentCaptor.capture());

        assertEquals("test@gmail.com", loginEntryArgumentCaptor.getValue().getEmail());
        assertEquals("encryptedPassword", loginEntryArgumentCaptor.getValue().getPassword());
    }

    @Test
    @SneakyThrows
    public void testAddUserToLoginEntry() {
        when(loginEntryRepository.existsById("test@gmail.com")).thenReturn(true);
        when(loginEntryRepository.findById("test@gmail.com")).thenReturn(Optional.of(LoginEntry.builder().email("test@gmail.com").users(new ArrayList<>()).build()));
        loginEntryService.addUserToLogin("test@gmail.com", Admin.builder().email("test@gmail.com").firstName("AdminFirstName").lastName("AdminLastName").build());
        ArgumentCaptor<LoginEntry> loginEntryArgumentCaptor = ArgumentCaptor.forClass(LoginEntry.class);
        verify(loginEntryRepository, times(1)).save(loginEntryArgumentCaptor.capture());
        assertEquals("test@gmail.com", loginEntryArgumentCaptor.getValue().getEmail());
        List<UserEntity> users = loginEntryArgumentCaptor.getValue().getUsers();
        assertEquals(1, users.size());
        UserEntity user = users.getFirst();
        assertEquals("AdminFirstName", user.getFirstName());
        assertEquals("AdminLastName", user.getLastName());
    }

    @Test
    public void testAddUserToLoginEmailDoesntExistException() {
        when(loginEntryRepository.existsById("test@gmail.com")).thenReturn(false);
        assertThrows(EmailDoesntExistException.class, () -> loginEntryService.addUserToLogin("test@gmail.com", Athlete.builder().build()));
    }

    @Test
    public void testUserLoginDoesntExistException() {
        when(loginEntryRepository.findById("test@gmail.com")).thenReturn(Optional.empty());
        assertThrows(EmailDoesntExistException.class, () -> loginEntryService.checkLogin("test@gmail.com", "testPassword"));
    }

    @Test
    public void testUserLoginDoesntMatchException() {
        when(loginEntryRepository.findById("test@gmail.com")).thenReturn(Optional.ofNullable(LoginEntry.builder().email("test@gmail.com").password("encryptedPassword").build()));
        when(bCryptPasswordEncoder.matches(eq("testPassword"), eq("encryptedPassword"))).thenReturn(false);
        assertThrows(LoginDoesntMatchException.class, () -> loginEntryService.checkLogin("test@gmail.com", "testPassword"));
    }

    @SneakyThrows
    @Test
    public void testSetPasswordThrowsException() {
        when(oneTimeCodeService.getEmailFromOneTimeCode(eq("testToken"), eq(OneTimeCodeType.SET_PASSWORD))).thenThrow(OneTimeCodeExpiredException.class);
        assertThrows(OneTimeCodeExpiredException.class, () -> loginEntryService.setPassword("testToken", "newPassword"));
    }
}
