package com.medals.medalsbackend.controllers;

import com.medals.medalsbackend.repository.OneTimeCodeRepository;
import com.medals.medalsbackend.service.user.login.LoginEntryService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private LoginEntryService loginEntryService;
  @Autowired
  private OneTimeCodeRepository oneTimeCodeRepository;

  @Test
  @SneakyThrows
  public void testUserCreation() {
    loginEntryService.createLoginEntry("test@example.org");
    String oneTimeCode = oneTimeCodeRepository.getAll().stream().filter(otc -> otc.authorizedEmail.equals("test@example.org")).findFirst().get().oneTimeCode;
    //mockMvc.perform(post("/api/v1/authorization/setPassword").).andDo(print()).andExpect(status().isOk())
  }
}
