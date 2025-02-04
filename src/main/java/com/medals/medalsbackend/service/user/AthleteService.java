package com.medals.medalsbackend.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.DummyData;
import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.entity.medals.MedalCollection;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.exceptions.AthleteNotFoundException;
import com.medals.medalsbackend.service.util.websockets.AthleteWebsocketMessageService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AthleteService {

    private final ObjectMapper objectMapper;
    private final AthleteWebsocketMessageService athleteWebsocketMessageService;
    private final UserEntityService userEntityService;

    @EventListener(ApplicationReadyEvent.class)
    public void instantiateDummyData() {
        log.info("Inserted {} dummy athletes", DummyData.ATHLETES.size());
        DummyData.ATHLETES.forEach(athlete -> {
            userEntityService.save(athlete.getEmail(), athlete);
        });
    }

    public UserEntity insertAthlete(AthleteDto athleteDto) {
        athleteDto.setId(null);
        Athlete athlete = (Athlete) userEntityService.save(athleteDto.getEmail(), objectMapper.convertValue(athleteDto, Athlete.class));
        log.info("Inserting Athlete: {}", athlete);
        athleteWebsocketMessageService.sendAthleteCreation(objectMapper.convertValue(athlete, AthleteDto.class));
        return athlete;
    }

    @SneakyThrows
    public Athlete[] getAthletes() {
        Athlete[] athletes = userEntityService.getAllAthletes().toArray(new Athlete[0]);
        log.info("Executing get all athletes: {}", athletes.length);
        return athletes;
    }

    public Athlete getAthlete(Long athleteId) throws AthleteNotFoundException {
        log.info("Executing get athlete by id {}", athleteId);
        try {
            return (Athlete) userEntityService.findById(athleteId).orElseThrow(() -> AthleteNotFoundException.fromAthleteId(athleteId));
        } catch (Exception e) {
            throw AthleteNotFoundException.fromAthleteId(athleteId);
        }
    }

    public void deleteAthlete(Long athleteId) throws AthleteNotFoundException {
        log.info("Executing delete athlete by id {}", athleteId);
        if (!userEntityService.existsById(athleteId)) {
            throw AthleteNotFoundException.fromAthleteId(athleteId);
        }
        athleteWebsocketMessageService.sendAthleteDelete(athleteId);
        userEntityService.deleteById(athleteId);
    }

    public void updateAthlete(Long athleteId, AthleteDto athleteDto) {
        athleteDto.setId(athleteId);
        Athlete savedAthlete = (Athlete) userEntityService.update(objectMapper.convertValue(athleteDto, Athlete.class));
        athleteWebsocketMessageService.sendAthleteUpdate(objectMapper.convertValue(savedAthlete, AthleteDto.class));
    }

    public MedalCollection getAthleteMedalCollection(Long athleteId) throws AthleteNotFoundException {
        log.info("Executing get athlete medal collection by id {}", athleteId);
        Athlete athlete = getAthlete(athleteId);
        return athlete.getMedalCollection();
    }
}
