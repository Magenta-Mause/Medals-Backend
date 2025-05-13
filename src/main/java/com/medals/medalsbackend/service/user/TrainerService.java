package com.medals.medalsbackend.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.DummyData;
import com.medals.medalsbackend.dto.TrainerDto;
import com.medals.medalsbackend.dto.authorization.TrainerAccessRequestDto;
import com.medals.medalsbackend.entity.initializedentity.InitializedEntity;
import com.medals.medalsbackend.entity.initializedentity.InitializedEntityType;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.users.Trainer;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.entity.users.UserType;
import com.medals.medalsbackend.exception.AthleteNotFoundException;
import com.medals.medalsbackend.exception.InternalException;
import com.medals.medalsbackend.exception.TrainerNotFoundException;
import com.medals.medalsbackend.repository.InitializedEntityRepository;
import com.medals.medalsbackend.service.notifications.NotificationService;
import com.medals.medalsbackend.service.onetimecode.OneTimeCodeCreationReason;
import com.medals.medalsbackend.service.user.login.jwt.JwtService;
import com.medals.medalsbackend.service.websockets.TrainerWebsocketMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Trainer createTrainer(Trainer trainer, String adminName) throws InternalException {
        userEntityService.save(trainer.getEmail(), trainer, adminName);
        return trainer;
    }

    public Trainer createTrainer(Trainer trainer) throws InternalException {
        return createTrainer(trainer, "the Medals Team");
    }

    public UserEntity insertTrainer(TrainerDto trainerDto, String adminName) throws InternalException {
        trainerDto.setId(null);
        Trainer trainer = (Trainer) userEntityService.save(trainerDto.getEmail(), objectMapper.convertValue(trainerDto, Trainer.class), OneTimeCodeCreationReason.ACCOUNT_INVITED, adminName);
        log.info("Inserting Trainer: {} (Inviting admin: {})", trainer, adminName);
        trainerWebsocketMessageService.sendTrainerCreate(objectMapper.convertValue(trainer, Trainer.class));
        return trainer;
    }

    public UserEntity insertTrainer(TrainerDto trainerDto) throws InternalException {
        return insertTrainer(trainerDto, "SYSTEM");
    }

    public List<Trainer> getAllTrainers() {
        return userEntityService.getAllTrainers();
    }

    public List<Trainer> getAllTrainersAssignedToAthlete(Long id) {
        return userEntityService.getAllTrainersAssignedToAthlete(id);
    }

    public void deleteTrainer(Long trainerId) throws Throwable {
        log.info("Executing delete trainer by id {}", trainerId);
        userEntityService.assertUserType(trainerId, UserType.TRAINER, TrainerNotFoundException.fromTrainerId(trainerId));
        userEntityService.deleteById(trainerId);
        trainerWebsocketMessageService.sendTrainerDelete(trainerId);
    }

    public Trainer getTrainer(Long trainerId) throws TrainerNotFoundException {
        log.info("Executing get trainer by id {}", trainerId);
        try {
            userEntityService.assertUserType(trainerId, UserType.TRAINER, TrainerNotFoundException.fromTrainerId(trainerId));
            return (Trainer) userEntityService.findById(trainerId).orElseThrow(() -> TrainerNotFoundException.fromTrainerId(trainerId));
        } catch (Exception e) {
            throw TrainerNotFoundException.fromTrainerId(trainerId);
        }
    }

    public Trainer updateTrainer(Long trainerId, String firstName, String lastName) throws TrainerNotFoundException {
        log.info("Updating trainer with ID: {}, firstName: {}, lastName: {}", trainerId, firstName, lastName);

        Optional<Trainer> trainerOptional = userEntityService.findTrainerById(trainerId);
        if (trainerOptional.isEmpty()) {
            throw TrainerNotFoundException.fromTrainerId(trainerId);
        }

        Trainer trainer = trainerOptional.get();
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);

        Trainer updatedTrainer = (Trainer) userEntityService.update(trainer);
        trainerWebsocketMessageService.sendTrainerUpdate(updatedTrainer);

        return updatedTrainer;
    }

    public void requestAthleteAccess(TrainerAccessRequestDto trainerAccessRequestDto) throws Exception {
        Long athleteId = trainerAccessRequestDto.getAthleteId();
        Long trainerId = trainerAccessRequestDto.getTrainerId();

        userEntityService.assertUserType(athleteId, UserType.ATHLETE, AthleteNotFoundException.fromAthleteId(athleteId));
        userEntityService.assertUserType(trainerId, UserType.TRAINER, TrainerNotFoundException.fromTrainerId(trainerId));

        Athlete requestedAthlete = (Athlete) userEntityService.findById(athleteId).orElseThrow(() -> AthleteNotFoundException.fromAthleteId(athleteId));
        Trainer trainer = (Trainer) userEntityService.findById(trainerId).orElseThrow(() -> TrainerNotFoundException.fromTrainerId(trainerId));

        log.info("Sending request to manage athlete {} from trainer {}", requestedAthlete, trainer);

        String trainerName = trainer.getFirstName() + " " + trainer.getLastName();
        String token = jwtService.buildTrainerAccessRequestToken(requestedAthlete.getEmail(), trainerAccessRequestDto, trainerName);
        notificationService.sendRequestAthleteNotification(requestedAthlete.getEmail(), token, trainerName);
    }

    public List<Athlete> searchAthletes(String athleteSearch) {
        return userEntityService.getAthletes(athleteSearch);
    }
}
