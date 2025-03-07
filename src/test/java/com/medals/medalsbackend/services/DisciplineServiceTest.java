package com.medals.medalsbackend.services;

import com.medals.medalsbackend.entity.performancerecording.Discipline;
import com.medals.medalsbackend.entity.performancerecording.DisciplineCategory;
import com.medals.medalsbackend.repository.DisciplineRatingMetricRepository;
import com.medals.medalsbackend.repository.DisciplineRepository;
import com.medals.medalsbackend.service.performancerecording.DisciplineService;
import com.medals.medalsbackend.service.websockets.DisciplineWebsocketMessagingService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class DisciplineServiceTest {
  @InjectMocks
  private DisciplineService disciplineService;
  @Mock
  private DisciplineRepository disciplineRepository;
  @Mock
  private DisciplineRatingMetricRepository disciplineRatingMetricRepository;
  @Mock
  private DisciplineWebsocketMessagingService disciplineWebsocketMessagingService;

  private static double convertTime(int minutes, int seconds) {
    return minutes * 60 + seconds;
  }

  @Test
  public void testGetAllDisciplines() {
    disciplineService.getDisciplines();
    verify(disciplineRepository, times(1)).findAll();
  }

  @SneakyThrows
  @Test
  public void testGetDisciplineById() {
    Discipline d = Discipline.builder().build();
    when(disciplineRepository.findById(1L)).thenReturn(Optional.of(d));
    Discipline dRes = disciplineService.getDisciplineById(1L);
    Assertions.assertEquals(d, dRes);
    verify(disciplineRepository, times(1)).findById(1L);
  }

  @Test
  public void testInsertDiscipline() {
    Discipline d = Discipline.builder()
      .category(DisciplineCategory.ENDURANCE)
      .moreBetter(true)
      .name("Test")
      .description("test")
      .unit(Discipline.Unit.meters)
      .build();
    when(disciplineRepository.save(any(Discipline.class))).thenReturn(d);
    Discipline dRes = disciplineService.insertDiscipline(d);
    verify(disciplineWebsocketMessagingService, times(1)).sendDisciplineCreation(d);
    verify(disciplineRepository, times(1)).save(d);
    Assertions.assertEquals(d, dRes);
  }

  @Test
  public void testDeleteDiscipline() {
    disciplineService.deleteDiscipline(1L);
    verify(disciplineWebsocketMessagingService, times(1)).sendDisciplineDeletion(1L);
    verify(disciplineRepository, times(1)).deleteById(1L);
  }

  @Test
  public void testUpdateDiscipline() {
    Discipline d = Discipline.builder()
      .id(1L)
      .build();
    when(disciplineRepository.save(d)).thenReturn(d);
    disciplineService.updateDiscipline(1L, d);
    verify(disciplineRepository, times(1)).save(d);
    verify(disciplineWebsocketMessagingService, times(1)).sendDisciplineUpdate(d);
  }

  @Test
  public void testDeleteDisciplineById() {
    disciplineService.deleteDiscipline(1L);
    verify(disciplineRepository, times(1)).deleteById(1L);
  }

}
