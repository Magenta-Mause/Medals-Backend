package com.medals.medalsbackend.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.DummyData;
import com.medals.medalsbackend.dto.PrunedAthleteDto;
import com.medals.medalsbackend.dto.TrainerDto;
import com.medals.medalsbackend.dto.authorization.TrainerAccessRequestDto;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.initializedentity.InitializedEntity;
import com.medals.medalsbackend.entity.initializedentity.InitializedEntityType;
import com.medals.medalsbackend.entity.users.Trainer;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.exception.AthleteNotFoundException;
import com.medals.medalsbackend.exception.TrainerNotFoundException;
import com.medals.medalsbackend.repository.InitializedEntityRepository;
import com.medals.medalsbackend.service.notifications.NotificationService;
import com.medals.medalsbackend.service.onetimecode.OneTimeCodeCreationReason;
import com.medals.medalsbackend.exception.InternalException;
import com.medals.medalsbackend.service.user.login.jwt.JwtService;
import com.medals.medalsbackend.service.websockets.TrainerWebsocketMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerService {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final UserEntityService userEntityService;
    private final TrainerWebsocketMessageService trainerWebsocketMessageService;
    private final NotificationService notificationService;
    @Value("${app.dummies.enabled}")
    private boolean insertDummies;
    private final InitializedEntityRepository initializedEntityRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Profile("!test")
    public void instantiateDummies() {
        if (!insertDummies) {
            return;
        }
        if (initializedEntityRepository.existsById(InitializedEntityType.Trainer)) {
            log.info("Trainer already initiated");
            return;
        }

        log.info("Inserting {} dummy trainers", DummyData.TRAINERS.size());
        DummyData.TRAINERS.forEach(trainer -> {
            try {
                createTrainer(trainer);
            } catch (InternalException internalException) {
                log.error(internalException.getMessage(), internalException);
            }
        });
        initializedEntityRepository.save(new InitializedEntity(InitializedEntityType.Trainer));
    }

    public Trainer createTrainer(Trainer trainer) throws InternalException {
        userEntityService.save(trainer.getEmail(), trainer);
        return trainer;
    }

    public UserEntity insertTrainer(TrainerDto trainerDto) throws InternalException {
        trainerDto.setId(null);
        Trainer trainer = (Trainer) userEntityService.save(trainerDto.getEmail(), objectMapper.convertValue(trainerDto, Trainer.class), OneTimeCodeCreationReason.ACCOUNT_INVITED);
        log.info("Inserting Trainer: {}", trainer);
        trainerWebsocketMessageService.sendTrainerCreation(objectMapper.convertValue(trainer, TrainerDto.class));
        return trainer;
    }

    public List<Trainer> getAllTrainers() {
        return userEntityService.getAllTrainers();
    }

    public void deleteTrainer(Long trainerId) throws TrainerNotFoundException {
        log.info("Executing delete trainer by id {}", trainerId);
        if (!userEntityService.existsById(trainerId)) {
            throw TrainerNotFoundException.fromTrainerId(trainerId);
        }
        trainerWebsocketMessageService.sendTrainerDelete(trainerId);
        userEntityService.deleteById(trainerId);
    }

    public Trainer getTrainer(Long trainerId) throws TrainerNotFoundException {
        log.info("Executing get trainer by id {}", trainerId);
        try {
            return (Trainer) userEntityService.findById(trainerId).orElseThrow(() -> TrainerNotFoundException.fromTrainerId(trainerId));
        } catch (Exception e) {
            throw TrainerNotFoundException.fromTrainerId(trainerId);
        }
    }

    public void updateTrainer(Long trainerId, TrainerDto trainerDto) {
        log.info("Updating trainer with ID: {}", trainerId);
        trainerDto.setId(trainerId);
        Trainer savedTrainer = (Trainer) userEntityService.update(objectMapper.convertValue(trainerDto, Trainer.class));
        trainerWebsocketMessageService.sendTrainerUpdate(objectMapper.convertValue(savedTrainer, TrainerDto.class));
    }

    public void requestAthleteAccess(TrainerAccessRequestDto trainerAccessRequestDto) throws AthleteNotFoundException, TrainerNotFoundException{
        Long athleteId = trainerAccessRequestDto.getAthleteId();
        Long trainerId = trainerAccessRequestDto.getTrainerId();
        Athlete inviteAthlete = (Athlete) userEntityService.findById(athleteId).orElseThrow(() -> AthleteNotFoundException.fromAthleteId(athleteId));
        Trainer trainer = (Trainer) userEntityService.findById(trainerId).orElseThrow(() -> TrainerNotFoundException.fromTrainerId(trainerId));
        log.info("Sending request to athlete {}", inviteAthlete);
        log.info("Request send by trainer {}", trainer);
        String token = jwtService.buildTrainerAccessRequestToken(inviteAthlete.getEmail(), trainerAccessRequestDto);
        notificationService.sendRequestAthleteNotification(inviteAthlete.getEmail(), token, trainer);
    }

    public List<PrunedAthleteDto> searchAthletes(String athleteSearch) {
        return userEntityService.getSimilarAthletes(athleteSearch);
    }
}
