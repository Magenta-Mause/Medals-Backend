package com.medals.medalsbackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.dto.authorization.LoginUserDto;
import com.medals.medalsbackend.dto.authorization.ResetPasswordDto;
import com.medals.medalsbackend.dto.authorization.SetPasswordDto;
import com.medals.medalsbackend.entity.onetimecode.OneTimeCode;
import com.medals.medalsbackend.entity.onetimecode.OneTimeCodeType;
import com.medals.medalsbackend.entity.users.Admin;
import com.medals.medalsbackend.repository.LoginEntryRepository;
import com.medals.medalsbackend.repository.OneTimeCodeRepository;
import com.medals.medalsbackend.security.jwt.JwtTokenBody;
import com.medals.medalsbackend.security.jwt.JwtUtils;
import com.medals.medalsbackend.service.user.AdminService;
import com.medals.medalsbackend.service.user.login.LoginEntryService;
import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    static
    class IdentityTokenApiResponse {
        private long status;
        private String httpStatus;
        private String message;
        private String timestamp;
        private String endpoint;
        private String data;
    }

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
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AdminService adminService;

    @SneakyThrows
    public String getIdentityToken(String email, String password) {
        LoginUserDto userLoginDto = LoginUserDto.builder()
            .email(email)
            .password(password)
            .build();

        Cookie refreshTokenCookie = mockMvc.perform(post("/api/v1/authorization/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userLoginDto))).andExpect(status().isOk()).andReturn().getResponse().getCookie("refreshToken");
        String result = mockMvc.perform(get("/api/v1/authorization/token").cookie(refreshTokenCookie)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        IdentityTokenApiResponse apiResponse = objectMapper.readValue(result, IdentityTokenApiResponse.class);
        return apiResponse.getData();
    }

    @SneakyThrows
    @BeforeEach
    public void initializeDemoUser() {
        loginEntryRepository.deleteAll();
        oneTimeCodeRepository.deleteAll();
        adminService.createAdmin(Admin.builder()
            .lastName("test")
            .firstName("test")
            .email("test@example.org")
            .build());

        String oneTimeCode = oneTimeCodeRepository.getAll().stream().filter(otc -> otc.authorizedEmail.equals("test@example.org")).findFirst().get().oneTimeCode;
        SetPasswordDto setUserPasswordDto = SetPasswordDto.builder()
            .password("newPassword")
            .oneTimeCode(oneTimeCode)
            .build();

        mockMvc.perform(post("/api/v1/authorization/setPassword").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(setUserPasswordDto))).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    public void testEndpointSecured() {
        mockMvc.perform(get("/api/v1/athletes")).andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void testUserCreation() throws ClassCastException {
        String identityToken = getIdentityToken("test@example.org", "newPassword");
        log.info("identityToken: {}", identityToken);
        Collection<Map<String, Object>> users = (Collection<Map<String, Object>>) jwtUtils.getTokenContentBody(identityToken, JwtTokenBody.TokenType.IDENTITY_TOKEN).get("users");
        Optional<Map<String, Object>> user = users.stream().findFirst();
        Assertions.assertTrue(user.isPresent());
        int userId = (int) user.get().get("id");
        mockMvc.perform(get("/api/v1/athletes")
            .header("Authorization", "Bearer " + identityToken)
            .header("X-Selected-User", String.valueOf(userId)))
            .andExpect(status().isOk());
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
