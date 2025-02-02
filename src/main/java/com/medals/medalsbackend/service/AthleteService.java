package com.medals.medalsbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.DummyData;
import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.entity.Athlete;
import com.medals.medalsbackend.entity.medal.MedalCollection;
import com.medals.medalsbackend.exceptions.AthleteNotFoundException;
import com.medals.medalsbackend.repository.AthleteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AthleteService {

  private final AthleteRepository athleteRepository;
  private final ObjectMapper objectMapper;
  private final AthleteWebsocketMessageService athleteWebsocketMessageService;

  @EventListener(ApplicationReadyEvent.class)
  public void instantiateDummyData() {
    log.info("Inserted {} dummy athletes", DummyData.ATHLETES.size());
    athleteRepository.saveAll(DummyData.ATHLETES);
  }

  public Athlete insertAthlete(AthleteDto athleteDto) {
    athleteDto.setId(null);
    Athlete savedAthlete = athleteRepository.save(objectMapper.convertValue(athleteDto, Athlete.class));
    log.info("Inserting Athlete: {}", savedAthlete);
    athleteWebsocketMessageService.sendAthleteCreation(objectMapper.convertValue(savedAthlete, AthleteDto.class));
    return savedAthlete;
  }

  public Athlete[] getAthletes() {
    log.info("Executing get all athletes");
    return athleteRepository.findAll().toArray(new Athlete[0]);
  }

  public Athlete getAthlete(Long athleteId) throws AthleteNotFoundException {
    log.info("Executing get athlete by id {}", athleteId);
    return athleteRepository.findById(athleteId).orElseThrow(() -> AthleteNotFoundException.fromAthleteId(athleteId));
  }

  public void deleteAthlete(Long athleteId) throws AthleteNotFoundException {
    log.info("Executing delete athlete by id {}", athleteId);
    if (!athleteRepository.existsById(athleteId)) {
      throw AthleteNotFoundException.fromAthleteId(athleteId);
    }
    athleteWebsocketMessageService.sendAthleteDelete(athleteId);
    athleteRepository.deleteById(athleteId);
  }

  public void updateAthlete(Long athleteId, AthleteDto athleteDto) {
    athleteDto.setId(athleteId);
    Athlete savedAthlete = athleteRepository.save(objectMapper.convertValue(athleteDto, Athlete.class));
    athleteWebsocketMessageService.sendAthleteUpdate(objectMapper.convertValue(savedAthlete, AthleteDto.class));
  }

  public MedalCollection getAthleteMedalCollection(Long athleteId) throws AthleteNotFoundException {
    log.info("Executing get athlete medal collection by id {}", athleteId);
    return athleteRepository.findById(athleteId).orElseThrow(() -> AthleteNotFoundException.fromAthleteId(athleteId)).getMedalCollection();
  }
}
