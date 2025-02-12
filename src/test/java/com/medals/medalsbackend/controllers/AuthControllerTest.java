package com.medals.medalsbackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.dto.authorization.LoginUserDto;
import com.medals.medalsbackend.dto.authorization.ResetPasswordDto;
import com.medals.medalsbackend.dto.authorization.SetPasswordDto;
import com.medals.medalsbackend.entity.util.oneTimeCodes.OneTimeCode;
import com.medals.medalsbackend.entity.util.oneTimeCodes.OneTimeCodeType;
import com.medals.medalsbackend.repository.LoginEntryRepository;
import com.medals.medalsbackend.repository.OneTimeCodeRepository;
import com.medals.medalsbackend.service.user.login.LoginEntryService;
import jakarta.servlet.http.Cookie;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private LoginEntryService loginEntryService;
  @Autowired
  private OneTimeCodeRepository oneTimeCodeRepository;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private LoginEntryRepository loginEntryRepository;

  @SneakyThrows
  public String getIdentityToken(String email, String password) {
    LoginUserDto userLoginDto = LoginUserDto.builder()
      .email(email)
      .password(password)
      .build();

    Cookie refreshTokenCookie = mockMvc.perform(post("/api/v1/authorization/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userLoginDto))).andDo(print()).andExpect(status().isOk()).andReturn().getResponse().getCookie("refreshToken");
    return mockMvc.perform(get("/api/v1/authorization/token").cookie(refreshTokenCookie)).andDo(print()).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  }

  @SneakyThrows
  @BeforeEach
  public void initializeDemoUser() {
    loginEntryRepository.deleteAll();
    oneTimeCodeRepository.deleteAll();
    loginEntryService.createLoginEntry("test@example.org");
    String oneTimeCode = oneTimeCodeRepository.getAll().stream().filter(otc -> otc.authorizedEmail.equals("test@example.org")).findFirst().get().oneTimeCode;
    SetPasswordDto setUserPasswordDto = SetPasswordDto.builder()
      .password("newPassword")
      .oneTimeCode(oneTimeCode)
      .build();

    LoginUserDto userLoginDto = LoginUserDto.builder()
      .email("test@example.org")
      .password("newPassword")
      .build();

    mockMvc.perform(post("/api/v1/authorization/setPassword").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(setUserPasswordDto))).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  public void testUserCreation() {
    getIdentityToken("test@example.org", "newPassword");
  }

  @SneakyThrows
  @Test
  public void testUserPasswordChange() {
    getIdentityToken("test@example.org", "newPassword");
    mockMvc.perform(post("/api/v1/authorization/resetPassword/test@example.org"));
    OneTimeCode oneTimeCode = oneTimeCodeRepository.getAll().stream().filter(code -> code.authorizedEmail.equals("test@example.org") && code.type == OneTimeCodeType.RESET_PASSWORD).findFirst().get();
    ResetPasswordDto resetPasswordDto = ResetPasswordDto.builder()
      .token(oneTimeCode.oneTimeCode)
      .password("newerPassword")
      .build();
    mockMvc.perform(post("/api/v1/authorization/resetPassword").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(resetPasswordDto))).andDo(print()).andExpect(status().isOk());
    getIdentityToken("test@example.org", "newerPassword");
    Assertions.assertThrows(Throwable.class, () -> getIdentityToken("test@example.org", "newPassword"));
    Assertions.assertDoesNotThrow(() -> getIdentityToken("test@example.org", "newerPassword"));
  }
}
