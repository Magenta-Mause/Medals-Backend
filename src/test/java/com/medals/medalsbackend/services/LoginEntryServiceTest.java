package com.medals.medalsbackend.services;

import com.medals.medalsbackend.entity.onetimecode.OneTimeCodeType;
import com.medals.medalsbackend.entity.users.Admin;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.users.LoginEntry;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.exception.onetimecode.OneTimeCodeExpiredException;
import com.medals.medalsbackend.repository.LoginEntryRepository;
import com.medals.medalsbackend.service.user.login.EmailDoesntExistException;
import com.medals.medalsbackend.service.user.login.LoginDoesntMatchException;
import com.medals.medalsbackend.service.user.login.LoginEntryService;
import com.medals.medalsbackend.service.user.login.jwt.JwtService;
import com.medals.medalsbackend.service.onetimecode.OneTimeCodeCreationReason;
import com.medals.medalsbackend.service.onetimecode.OneTimeCodeService;
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

  @Test
  @SneakyThrows
  public void testPasswordReset() {
    when(oneTimeCodeService.getEmailFromOneTimeCode(eq("test"), eq(OneTimeCodeType.SET_PASSWORD))).thenReturn("admin@example.org");
    when(loginEntryRepository.findById(eq("admin@example.org"))).thenReturn(Optional.of(LoginEntry.builder().email("admin@example.org").password("oldPassword").build()));
    loginEntryService.createLoginEntry("admin@example.org", OneTimeCodeCreationReason.ACCOUNT_CREATED, "mock");
    loginEntryService.setPassword("test", "newPassword");

    ArgumentCaptor<LoginEntry> loginEntryArgumentCaptor = ArgumentCaptor.forClass(LoginEntry.class);
    verify(loginEntryRepository, times(2)).save(loginEntryArgumentCaptor.capture());
    assertEquals("admin@example.org", loginEntryArgumentCaptor.getValue().getEmail());
  }

  @Test
  @SneakyThrows
  public void testLoginEntryCreationTriggersEmailSending() {
    when(loginEntryRepository.existsById(any())).thenReturn(false);
    loginEntryService.createLoginEntry("test@gmail.com", OneTimeCodeCreationReason.ACCOUNT_CREATED, "mock");
    verify(oneTimeCodeService, times(1)).createSetPasswordToken(eq("test@gmail.com"), eq(OneTimeCodeCreationReason.ACCOUNT_CREATED), eq("mock"));
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
