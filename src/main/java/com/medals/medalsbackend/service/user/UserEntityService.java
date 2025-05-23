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
    public UserEntity save(String email, UserEntity userEntity, OneTimeCodeCreationReason reason, String invitingParty) throws InternalException {
        try {
            loginEntryService.createLoginEntry(email, reason, invitingParty);
        } catch (EmailAlreadyExistsException ignored) {
        }

        try {
            return loginEntryService.addUserToLogin(email, userEntity);
        } catch (EmailDoesntExistException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public UserEntity save(String email, UserEntity userEntity, String invitingParty) throws InternalException {
        return save(email, userEntity, OneTimeCodeCreationReason.ACCOUNT_CREATED, invitingParty);
    }

    public UserEntity update(UserEntity userEntity) {
        return userEntityRepository.save(userEntity);
    }

    public Optional<UserEntity> findById(long id) {
        return userEntityRepository.findById(id);
    }

    public Optional<Admin> findAdminById(long id) {
        Optional<UserEntity> userEntityOptional = userEntityRepository.findById(id);
        if (userEntityOptional.isPresent() && userEntityOptional.get() instanceof Admin) {
            return Optional.of((Admin) userEntityOptional.get());
        }
        return Optional.empty();
    }

    public Optional<Trainer> findTrainerById(long id) {
        Optional<UserEntity> userEntityOptional = userEntityRepository.findById(id);
        if (userEntityOptional.isPresent() && userEntityOptional.get() instanceof Trainer) {
            return Optional.of((Trainer) userEntityOptional.get());
        }
        return Optional.empty();
    }

    public Optional<Athlete> findAthleteById(long id) {
        Optional<UserEntity> userEntityOptional = userEntityRepository.findById(id);
        if (userEntityOptional.isPresent() && userEntityOptional.get() instanceof Athlete) {
            return Optional.of((Athlete) userEntityOptional.get());
        }
        return Optional.empty();
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
        userEntityRepository.deleteById(id);
        log.info("Deleted user {}", userEntity);
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
}
