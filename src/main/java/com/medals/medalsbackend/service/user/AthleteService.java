package com.medals.medalsbackend.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.DummyData;
import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.entity.initializedentity.InitializedEntity;
import com.medals.medalsbackend.entity.initializedentity.InitializedEntityType;
import com.medals.medalsbackend.entity.medals.MedalCollection;
import com.medals.medalsbackend.entity.swimCertificate.SwimCertificateType;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.users.Trainer;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.entity.users.UserType;
import com.medals.medalsbackend.exception.AthleteNotFoundException;
import com.medals.medalsbackend.exception.InternalException;
import com.medals.medalsbackend.exception.TrainerNotFoundException;
import com.medals.medalsbackend.repository.AthleteAccessRequestRepository;
import com.medals.medalsbackend.repository.InitializedEntityRepository;
import com.medals.medalsbackend.repository.PerformanceRecordingRepository;
import com.medals.medalsbackend.repository.UserEntityRepository;
import com.medals.medalsbackend.security.jwt.JwtUtils;
import com.medals.medalsbackend.service.websockets.AthleteWebsocketMessageService;
import com.medals.medalsbackend.service.websockets.ManagingTrainerWebsocketService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AthleteService {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;
    private final AthleteWebsocketMessageService athleteWebsocketMessageService;
    private final UserEntityService userEntityService;
    private final UserEntityRepository userEntityRepository;
    private final TrainerService trainerService;
    private final PerformanceRecordingRepository performanceRecordingRepository;
    private final AthleteAccessRequestRepository athleteAccessRequestRepository;
    private final ManagingTrainerWebsocketService managingTrainerWebsocketService;
    @Value("${app.dummies.enabled}")
    private boolean insertDummies;
    private final InitializedEntityRepository initializedEntityRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Profile("!test")
    public void instantiateDummies() {
        if (!insertDummies) {
            return;
        }
        if (initializedEntityRepository.existsById(InitializedEntityType.Athlete)) {
            log.info("Athletes already initiated");
            return;
        }

        log.info("Inserting {} dummy athletes", DummyData.ATHLETES.size());
        DummyData.ATHLETES.forEach(athlete -> {
            try {
                userEntityService.save(athlete.getEmail(), athlete, "SYSTEM");
            } catch (InternalException e) {
                throw new RuntimeException(e);
            }
        });
        initializedEntityRepository.save(new InitializedEntity(InitializedEntityType.Athlete));
    }

    public UserEntity insertAthlete(AthleteDto athleteDto, String trainerName) throws InternalException {
        athleteDto.setId(null);

        if (existsByBirthdateAndEmail(athleteDto.getEmail(), athleteDto.getBirthdate())) {
            throw new InternalException("An athlete with the same email and birthdate already exists.");
        }

        Athlete athlete = (Athlete) userEntityService.save(
            athleteDto.getEmail(),
            objectMapper.convertValue(athleteDto, Athlete.class),
            trainerName
        );

        log.info("Inserting Athlete: {} (Invited by: {})", athlete, trainerName);
        athleteWebsocketMessageService.sendAthleteCreation(objectMapper.convertValue(athlete, AthleteDto.class));
        return athlete;
    }

    public UserEntity insertAthlete(AthleteDto athleteDto) throws InternalException {
        return insertAthlete(athleteDto, "SYSTEM");
    }

    public Athlete[] getAthletes() {
        return userEntityService.getAllAthletes().toArray(new Athlete[0]);
    }

    public Athlete[] getAthletesAssignedToTrainer(Long id) {
        return userEntityService.getAthletesAssignedToTrainer(id).toArray(new Athlete[0]);
    }

    public Athlete getAthlete(Long athleteId) throws AthleteNotFoundException {
        try {
            userEntityService.assertUserType(athleteId, UserType.ATHLETE, AthleteNotFoundException.fromAthleteId(athleteId));
            return (Athlete) userEntityService.findById(athleteId).orElseThrow(() -> AthleteNotFoundException.fromAthleteId(athleteId));
        } catch (Exception e) {
            throw AthleteNotFoundException.fromAthleteId(athleteId);
        }
    }

    public boolean existsByBirthdateAndEmail(String email, LocalDate birthdate) {
        return userEntityRepository.findAthleteByEmailAndBirthdate(email, birthdate).isPresent();
    }

    @Transactional
    public void deleteAthlete(Long athleteId) throws Exception {
        log.info("Executing delete athlete by id {}", athleteId);
        userEntityService.assertUserType(athleteId, UserType.ATHLETE, AthleteNotFoundException.fromAthleteId(athleteId));
        performanceRecordingRepository.deleteByAthleteId(athleteId);
        athleteWebsocketMessageService.sendAthleteDelete(athleteId);
        userEntityService.deleteById(athleteId);
    }

    public void updateAthlete(Long athleteId, AthleteDto athleteDto) throws Exception {
        log.info("Updating athlete with ID: {}", athleteId);
        userEntityService.assertUserType(athleteId, UserType.ATHLETE, AthleteNotFoundException.fromAthleteId(athleteId));
        athleteDto.setId(athleteId);
        Athlete savedAthlete = (Athlete) userEntityService.update(objectMapper.convertValue(athleteDto, Athlete.class));
        athleteWebsocketMessageService.sendAthleteUpdate(objectMapper.convertValue(savedAthlete, AthleteDto.class));
    }

    public MedalCollection getAthleteMedalCollection(Long athleteId) throws AthleteNotFoundException {
        Athlete athlete = getAthlete(athleteId);
        return athlete.getMedalCollection();
    }

    public AthleteDto truncateAthlete(Athlete athlete) {
        return AthleteDto.builder()
            .id(athlete.getId())
            .firstName(athlete.getFirstName())
            .lastName(athlete.getLastName())
            .gender(athlete.getGender())
            .birthdate(athlete.getBirthdate())
            .hasAccess(false)
            .build();
    }

    public Athlete updateSwimmingCertificate(Long athleteId, SwimCertificateType certificate) throws AthleteNotFoundException {
        Athlete athlete = getAthlete(athleteId);
        athlete.setSwimmingCertificate(certificate);
        Athlete updated = (Athlete) userEntityService.update(athlete);
        athleteWebsocketMessageService.sendAthleteUpdate(objectMapper.convertValue(updated, AthleteDto.class));
        return updated;
    }

    public boolean checkExistence(long athleteId) {
        try {
            return userEntityService.findById(athleteId).orElseThrow().getType().equals(UserType.ATHLETE);
        } catch (Exception e) {
            return false;
        }
    }

    public void removeConnection(Long trainerId, Long athleteId) throws Exception {
        Athlete athlete = (Athlete) userEntityService.findById(athleteId).orElseThrow(() -> AthleteNotFoundException.fromAthleteId((athleteId)));
        Trainer trainer = (Trainer) userEntityService.findById(trainerId).orElseThrow(() -> TrainerNotFoundException.fromTrainerId(trainerId));

        log.info("removing connection between athlete: {} and trainer: {}", athlete, trainer);
        athlete.getTrainersAssignedTo().removeIf(assignedTrainer -> assignedTrainer.equals(trainer));
        trainer.getAssignedAthletes().removeIf(assignedAthlete -> assignedAthlete.equals(athlete));

        userEntityService.update(athlete);
        userEntityService.update(trainer);

        athleteWebsocketMessageService.sendAthleteRemoveConnection(athleteId, trainerId);
        managingTrainerWebsocketService.sendManagingTrainerDeletion(athleteId, trainerId);
    }
}
