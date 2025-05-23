package com.medals.medalsbackend.services;

import com.medals.medalsbackend.dto.PrunedAthleteDto;
import com.medals.medalsbackend.dto.TrainerDto;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.users.Trainer;
import com.medals.medalsbackend.repository.UserEntityRepository;
import com.medals.medalsbackend.service.user.UserEntityService;
import com.medals.medalsbackend.service.user.login.LoginEntryService;
import com.medals.medalsbackend.service.onetimecode.OneTimeCodeCreationReason;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserEntityServiceTest {

    @Mock
    private LoginEntryService loginEntryService;

    @Mock
    private UserEntityRepository userEntityRepository;

    @InjectMocks
    private UserEntityService userEntityService;

    @SneakyThrows
    @Test
    public void testUserEntityCreationCreatesLoginEntry() {
        userEntityService.save("test@gmail.com", Trainer.builder().email("test@gmail.com").firstName("tom").lastName("tailor").build(), "mock");

        verify(loginEntryService, times(1)).createLoginEntry(eq("test@gmail.com"), eq(OneTimeCodeCreationReason.ACCOUNT_CREATED), eq("mock"));
        verify(loginEntryService, times(1)).addUserToLogin(eq("test@gmail.com"), any());
    }

    @Test
    void testUserEntitySearchAthleteWithFirstname() {
        Athlete athlete = Athlete.builder()
                .id(1L)
                .firstName("John")
                .lastName("Reiter")
                .birthdate(LocalDate.of(2010, 3, 7))
                .build();


        when(userEntityRepository.searchGeneric("John")).thenReturn(
                List.of(
                        athlete
                )
        );

        List<Athlete> result = userEntityService.getAthletes("John");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getFirstName()).isEqualTo("John");
        assertThat(result.getFirst().getLastName()).isEqualTo("Reiter");
        assertThat(result.getFirst().getBirthdate()).isEqualTo("2010-03-07");
    }

    @Test
    void testUserEntitySearchAthleteWithLastname() {
        Athlete athlete = Athlete.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Reiter")
                .birthdate(LocalDate.of(2010, 3, 7))
                .build();

        Athlete athlete2 = Athlete.builder()
                .id(1L)
                .firstName("John")
                .lastName("Reiter")
                .birthdate(LocalDate.of(2010, 3, 7))
                .build();


        when(userEntityRepository.searchGeneric("Reiter")).thenReturn(
                List.of(
                        athlete,
                        athlete2
                )
        );

        List<Athlete> result = userEntityService.getAthletes("Reiter");

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().getFirstName()).isEqualTo("Jane");
        assertThat(result.getFirst().getLastName()).isEqualTo("Reiter");
        assertThat(result.getFirst().getBirthdate()).isEqualTo("2010-03-07");
        assertThat(result.get(1).getFirstName()).isEqualTo("John");
        assertThat(result.get(1).getLastName()).isEqualTo("Reiter");
        assertThat(result.get(1).getBirthdate()).isEqualTo("2010-03-07");
    }

    @Test
    void testUserEntitySearchAthleteWithFullname() {
        Athlete athlete = Athlete.builder()
                .id(1L)
                .firstName("John")
                .lastName("Reiter")
                .birthdate(LocalDate.of(2010, 3, 7))
                .build();

        when(userEntityRepository.searchGeneric("John Reiter")).thenReturn(
                List.of(athlete)
        );

        List<Athlete> result = userEntityService.getAthletes("John Reiter");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getFirstName()).isEqualTo("John");
        assertThat(result.getFirst().getLastName()).isEqualTo("Reiter");
        assertThat(result.getFirst().getBirthdate()).isEqualTo("2010-03-07");
    }

    @Test
    void testUserEntitySearchAthleteWithEmail() {
        Athlete athlete = Athlete.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Reiter")
                .birthdate(LocalDate.of(2010, 3, 7))
                .build();

        Athlete athlete2 = Athlete.builder()
                .id(1L)
                .firstName("John")
                .lastName("Reiter")
                .birthdate(LocalDate.of(2010, 3, 7))
                .build();


        when(userEntityRepository.searchGeneric("@example.org")).thenReturn(
                List.of(
                        athlete,
                        athlete2
                )
        );

        List<Athlete> result = userEntityService.getAthletes("@example.org");

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().getFirstName()).isEqualTo("Jane");
        assertThat(result.getFirst().getLastName()).isEqualTo("Reiter");
        assertThat(result.getFirst().getBirthdate()).isEqualTo("2010-03-07");
        assertThat(result.get(1).getFirstName()).isEqualTo("John");
        assertThat(result.get(1).getLastName()).isEqualTo("Reiter");
        assertThat(result.get(1).getBirthdate()).isEqualTo("2010-03-07");
    }

    @Test
    void testUserEntitySearchAthleteWithWrongName() {
        when(userEntityRepository.searchGeneric("Test")).thenReturn(
                List.of()
        );

        List<Athlete> result = userEntityService.getAthletes("Test");

        assertThat(result).isEmpty();
    }
}
