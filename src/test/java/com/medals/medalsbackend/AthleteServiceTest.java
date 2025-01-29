package com.medals.medalsbackend;

import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.entity.Athlete;
import com.medals.medalsbackend.service.AthleteService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AthleteServiceTest {

    @Autowired
    private AthleteService athleteService;

    @Test
    public void testAthleteCreation() {
        // Prepare
        AthleteDto athleteDto = AthleteDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@doe.com")
                .birthdate(LocalDate.of(2005, 5, 13))
                .gender(Athlete.Gender.MALE)
                .build();

        // Act
        Athlete athlete = athleteService.insertAthlete(athleteDto);

        // Assert
        Assertions.assertThat(athlete).isNotNull();
        Assertions.assertThat(athlete.getEmail()).isEqualTo("john@doe.com");
        Assertions.assertThat(athlete.getFirstName()).isEqualTo("John");
        Assertions.assertThat(athlete.getLastName()).isEqualTo("Doe");
        Assertions.assertThat(athlete.getBirthdate()).isEqualTo(LocalDate.of(2005, 5, 13));
    }

    @Test
    public void testAthleteEmailValidation() {
        // Prepare
        AthleteDto athleteDto = AthleteDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@doe")
                .birthdate(LocalDate.of(2005, 5, 13))
                .gender(Athlete.Gender.FEMALE)
                .build();

        // Act & Assert
        Athlete athlete = athleteService.insertAthlete(athleteDto);
        System.out.println(athlete.toString());
    }
}
