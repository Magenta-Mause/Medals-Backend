package com.medals.medalsbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.DummyData;
import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.entity.Athlete;
import com.medals.medalsbackend.entity.medal.MedalCollection;
import com.medals.medalsbackend.exceptions.AthleteNotFoundException;
import com.medals.medalsbackend.repository.AthleteRepository;
import jakarta.validation.Valid;
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

    @EventListener(ApplicationReadyEvent.class)
    public void instantiateDummyData() {
        log.info("Inserted {} dummy athletes", DummyData.ATHLETES.size());
        athleteRepository.saveAll(DummyData.ATHLETES);
    }

    public Athlete insertAthlete(@Valid AthleteDto athleteDto) {
        return athleteRepository.save(objectMapper.convertValue(athleteDto, Athlete.class));
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
        athleteRepository.deleteById(athleteId);
    }

    public MedalCollection getAthleteMedalCollection(Long athleteId) throws AthleteNotFoundException {
        log.info("Executing get athlete medal collection by id {}", athleteId);
        return athleteRepository.findById(athleteId).orElseThrow(() -> AthleteNotFoundException.fromAthleteId(athleteId)).getMedalCollection();
    }
}
