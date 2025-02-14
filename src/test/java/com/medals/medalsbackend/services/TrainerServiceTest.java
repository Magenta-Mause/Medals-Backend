package com.medals.medalsbackend.services;

import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.dto.TrainerDto;
import com.medals.medalsbackend.entity.users.Trainer;
import com.medals.medalsbackend.service.user.TrainerService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

@Slf4j
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TrainerServiceTest {

  @Autowired
  private TrainerService trainerService;

  @SneakyThrows
  @Test
  public void testTrainerCreation() {
    TrainerDto trainer = TrainerDto.builder()
      .firstName("John")
      .lastName("Doe")
      .email("john@doe.com")
      .build();

    Trainer resultAthlete = (Trainer) trainerService.insertTrainer(trainer);

    Assertions.assertThat(resultAthlete).isNotNull();
    Assertions.assertThat(resultAthlete.getId()).isNotNull();
    Assertions.assertThat(resultAthlete.getEmail()).isEqualTo("john@doe.com");
    Assertions.assertThat(resultAthlete.getFirstName()).isEqualTo("John");
    Assertions.assertThat(resultAthlete.getLastName()).isEqualTo("Doe");
  }

  @Test
  public void testTrainerNotFoundException() {
    Assertions.assertThatThrownBy(() -> trainerService.getTrainer(400L));
  }

  @SneakyThrows
  @Test
  public void testAthleteUpdate() {
    TrainerDto trainerDto = TrainerDto.builder()
      .firstName("John")
      .lastName("Doe")
      .email("john@doe.com")
      .build();

    Trainer trainerBeforeChange = (Trainer) trainerService.insertTrainer(trainerDto);
    Long trainerId = trainerBeforeChange.getId();
    Assertions.assertThat(trainerBeforeChange.getFirstName()).isEqualTo("John");
    trainerDto.setFirstName("Jane");
    trainerService.updateTrainer(trainerId, trainerDto);
    Trainer trainerAfterChange = trainerService.getTrainer(trainerId);
    Assertions.assertThat(trainerAfterChange.getFirstName()).isEqualTo("Jane");
  }

  @SneakyThrows
  @Test
  public void testTrainerDelete() {
    TrainerDto trainerDto = TrainerDto.builder()
      .firstName("Franziska")
      .lastName("Fronk")
      .email("test@test.de")
      .build();

    Trainer Trainer = (Trainer) trainerService.insertTrainer(trainerDto);
    log.info("Trainer inserted: {}", Trainer);
    Long athleteId = Trainer.getId();
    trainerService.deleteTrainer(athleteId);
    Assertions.assertThatThrownBy(() -> trainerService.getTrainer(athleteId));
  }
}
