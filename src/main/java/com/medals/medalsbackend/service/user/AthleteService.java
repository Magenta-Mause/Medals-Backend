package com.medals.medalsbackend.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.DummyData;
import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.entity.medals.MedalCollection;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.exception.AthleteNotFoundException;
import com.medals.medalsbackend.exception.InternalException;
import com.medals.medalsbackend.service.util.websockets.AthleteWebsocketMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AthleteService {

    private final ObjectMapper objectMapper;
    private final AthleteWebsocketMessageService athleteWebsocketMessageService;
    private final UserEntityService userEntityService;
    private final Environment environment;
    @Value("${app.dummies.enabled}")
    private boolean insertDummies;

    @EventListener(ApplicationReadyEvent.class)
    @Profile("!test")
    public void instantiateDummies() {
        if (!insertDummies) {
            return;
        }

        log.info("Inserting {} dummy athletes", DummyData.ATHLETES.size());
        DummyData.ATHLETES.forEach(athlete -> {
            try {
                userEntityService.save(athlete.getEmail(), athlete);
            } catch (InternalException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public UserEntity insertAthlete(AthleteDto athleteDto) throws InternalException {
        athleteDto.setId(null);
        Athlete athlete = (Athlete) userEntityService.save(athleteDto.getEmail(), objectMapper.convertValue(athleteDto, Athlete.class));
        log.info("Inserting Athlete: {}", athlete);
        athleteWebsocketMessageService.sendAthleteCreation(objectMapper.convertValue(athlete, AthleteDto.class));
        return athlete;
    }

    public Athlete[] getAthletes() {
        return userEntityService.getAllAthletes().toArray(new Athlete[0]);
    }

    public Athlete getAthlete(Long athleteId) throws AthleteNotFoundException {
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
        log.info("Updating athlete with ID: {}", athleteId);
        athleteDto.setId(athleteId);
        Athlete savedAthlete = (Athlete) userEntityService.update(objectMapper.convertValue(athleteDto, Athlete.class));
        athleteWebsocketMessageService.sendAthleteUpdate(objectMapper.convertValue(savedAthlete, AthleteDto.class));
    }

    public MedalCollection getAthleteMedalCollection(Long athleteId) throws AthleteNotFoundException {
        Athlete athlete = getAthlete(athleteId);
        return athlete.getMedalCollection();
    }
}
