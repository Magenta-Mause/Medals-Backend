package com.medals.medalsbackend.service.user;

import com.medals.medalsbackend.entity.users.*;
import com.medals.medalsbackend.exception.AthleteNotFoundException;
import com.medals.medalsbackend.exception.InternalException;
import com.medals.medalsbackend.exception.TrainerNotFoundException;
import com.medals.medalsbackend.repository.UserEntityRepository;
import com.medals.medalsbackend.service.onetimecode.OneTimeCodeCreationReason;
import com.medals.medalsbackend.service.user.login.EmailAlreadyExistsException;
import com.medals.medalsbackend.service.user.login.EmailDoesntExistException;
import com.medals.medalsbackend.service.user.login.LoginEntryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEntityService {
    private final UserEntityRepository userEntityRepository;
    private final LoginEntryService loginEntryService;

    @Transactional
    public UserEntity save(String email, UserEntity userEntity, OneTimeCodeCreationReason reason) throws InternalException {
        try {
            loginEntryService.createLoginEntry(email, reason);
        } catch (EmailAlreadyExistsException ignored) {
        }

        try {
            return loginEntryService.addUserToLogin(email, userEntity);
        } catch (EmailDoesntExistException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public UserEntity save(String email, UserEntity userEntity) throws InternalException {
        return save(email, userEntity, OneTimeCodeCreationReason.ACCOUNT_CREATED);
    }

    public UserEntity update(UserEntity userEntity) {
        return userEntityRepository.save(userEntity);
    }

    public Optional<UserEntity> findById(long id) {
        return userEntityRepository.findById(id);
    }

    public List<UserEntity> getAll() {
        return userEntityRepository.findAll();
    }

    public List<UserEntity> getAllByEmail(String email) {
        return userEntityRepository.getAllByEmail(email);
    }

    public List<Athlete> getAllAthletes() {
        return userEntityRepository.findAllAthletes();
    }

    public List<Trainer> getAllTrainers() {
        return userEntityRepository.findAllTrainers();
    }

    public List<Trainer> getAllTrainersAssignedToAthlete(Long id) {
        return userEntityRepository.findAllTrainersAssignedToAthlete(id);
    }

    public List<Admin> getAllAdmins() {
        return userEntityRepository.findAllAdmins();
    }

    public void deleteById(Long id) {
        UserEntity userEntity = userEntityRepository.findById(id).orElseThrow();
        userEntityRepository.delete(userEntity);
        if (userEntityRepository.getAllByEmail(userEntity.getEmail()).isEmpty()) {
            loginEntryService.deleteEntry(userEntity.getEmail());
        }
    }

    public void assertUserType(Long userId, UserType userType, Exception e) throws Exception {
        if (!userEntityRepository.findById(userId).orElseThrow(() -> e).getType().equals(userType)) {
            throw e;
        }
    }

    public List<Athlete> getAthletes(String athleteSearch) {
        return userEntityRepository.searchGeneric(athleteSearch);
    }

    public boolean existsById(Long id) {
        return userEntityRepository.existsById(id);
    }

    public List<Athlete> getAthletesAssignedToTrainer(Long id) {
        return userEntityRepository.findAthletes(id);
    }

    public void removeConnection(Long trainerId, Long athleteId) throws Exception {
        assertUserType(athleteId, UserType.ATHLETE, AthleteNotFoundException.fromAthleteId(athleteId));
        assertUserType(trainerId, UserType.TRAINER, TrainerNotFoundException.fromTrainerId(trainerId));

        Athlete athlete = (Athlete) findById(athleteId).orElseThrow(() -> AthleteNotFoundException.fromAthleteId((athleteId)));
        Trainer trainer = (Trainer) findById(trainerId).orElseThrow(() -> TrainerNotFoundException.fromTrainerId(trainerId));

        log.info("The connection between athlete: {} and trainer: {} is getting removed", athlete, trainer);
        athlete.getTrainersAssignedTo().removeIf(assignedTrainer -> assignedTrainer.equals(trainer));
        trainer.getAssignedAthletes().removeIf(assignedAthlete -> assignedAthlete.equals(athlete));

        athlete.setTrainersAssignedTo(athlete.getTrainersAssignedTo());
        trainer.setAssignedAthletes(trainer.getAssignedAthletes());

        update(athlete);
        update(trainer);
    }
}
