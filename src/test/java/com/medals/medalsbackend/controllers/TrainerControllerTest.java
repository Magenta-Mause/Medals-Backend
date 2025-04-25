package com.medals.medalsbackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.dto.TrainerDto;
import com.medals.medalsbackend.entity.users.Admin;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.service.authorization.AuthorizationService;
import com.medals.medalsbackend.service.authorization.ForbiddenException;
import com.medals.medalsbackend.service.authorization.NoAuthenticationFoundException;
import com.medals.medalsbackend.service.user.TrainerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "disableAuth"})
public class TrainerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private TrainerService trainerService;

  private static TrainerDto testTrainer;

  @MockitoBean
  AuthorizationService authorizationService;

  @BeforeAll
  public static void setup() {
    testTrainer = TrainerDto.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .build();
  }

  @BeforeEach
  public void setupEach() throws NoAuthenticationFoundException, ForbiddenException {
    UserEntity mockAdmin = new Admin();
    mockAdmin.setFirstName("Mock");
    mockAdmin.setLastName("Admin");

    when(authorizationService.getSelectedUser()).thenReturn(mockAdmin);
    doNothing().when(authorizationService).assertRoleIn(anyList());
  }

  @Test
  public void testCreateTrainer() throws Exception {
    mockMvc.perform(post("/api/v1/trainers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testTrainer)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.first_name").value("John"))
            .andExpect(jsonPath("$.data.last_name").value("Doe"));
  }

  @Test
  public void testValidateTrainers() throws Exception {
    mockMvc.perform(post("/api/v1/trainers/validate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Collections.singletonList(testTrainer))))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].email").value("john.doe@example.com"));
  }

  @Test
  public void testGetTrainer() throws Exception {
    trainerService.insertTrainer(testTrainer);

    mockMvc.perform(get("/api/v1/trainers/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.first_name").value("John"))
            .andExpect(jsonPath("$.data.last_name").value("Doe"));
  }

  @Test
  public void testDeleteTrainer() throws Exception {
    trainerService.insertTrainer(testTrainer);

    mockMvc.perform(delete("/api/v1/trainers/1"))
            .andDo(print())
            .andExpect(status().isAccepted());
  }
}
