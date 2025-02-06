package com.medals.medalsbackend;

import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.service.user.AthleteService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@Slf4j
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AthleteServiceTest {

  @Autowired
  private AthleteService athleteService;

  @SneakyThrows
  @Test
  public void testAthleteCreation() {
    // Prepare
    AthleteDto athlete = AthleteDto.builder()
      .firstName("John")
      .lastName("Doe")
      .email("john@doe.com")
      .birthdate(LocalDate.of(2005, 5, 13))
      .gender(Athlete.Gender.MALE)
      .build();

    // Act
    Athlete resultAthlete = (Athlete) athleteService.insertAthlete(athlete);

    // Assert
    Assertions.assertThat(resultAthlete).isNotNull();
    Assertions.assertThat(resultAthlete.getEmail()).isEqualTo("john@doe.com");
    Assertions.assertThat(resultAthlete.getFirstName()).isEqualTo("John");
    Assertions.assertThat(resultAthlete.getLastName()).isEqualTo("Doe");
    Assertions.assertThat(resultAthlete.getBirthdate()).isEqualTo(LocalDate.of(2005, 5, 13));
  }

  @Test
  public void testAthleteNotFoundException() {
    Assertions.assertThatThrownBy(() -> athleteService.getAthlete(400L));
  }

  @SneakyThrows
  @Test
  public void testAthleteUpdate() {
    AthleteDto athleteDto = AthleteDto.builder()
      .firstName("John")
      .lastName("Doe")
      .email("john@doe.com")
      .birthdate(LocalDate.of(2005, 5, 13))
      .gender(Athlete.Gender.MALE)
      .build();

    Athlete athleteBeforeChange = (Athlete) athleteService.insertAthlete(athleteDto);
    Long athleteId = athleteBeforeChange.getId();
    Assertions.assertThat(athleteBeforeChange.getBirthdate()).isEqualTo(LocalDate.of(2005, 5, 13));
    athleteDto.setBirthdate(LocalDate.of(2005, 4, 13));
    athleteService.updateAthlete(athleteId, athleteDto);
    Athlete athleteAfterChange = athleteService.getAthlete(athleteId);
    Assertions.assertThat(athleteAfterChange.getBirthdate()).isEqualTo(LocalDate.of(2005, 4, 13));
  }

  @SneakyThrows
  @Test
  public void testAthleteDelete() {
    AthleteDto athleteDto = AthleteDto.builder()
      .firstName("Franziska")
      .lastName("Fronk")
      .email("test@test.de")
      .birthdate(LocalDate.of(2005, 5, 13))
      .gender(Athlete.Gender.MALE)
      .build();

    Athlete athlete = (Athlete) athleteService.insertAthlete(athleteDto);
    log.info("Athlete inserted: {}", athlete);
    Long athleteId = athlete.getId();
    athleteService.deleteAthlete(athleteId);
    Assertions.assertThatThrownBy(() -> athleteService.getAthlete(athleteId));
  }
}
