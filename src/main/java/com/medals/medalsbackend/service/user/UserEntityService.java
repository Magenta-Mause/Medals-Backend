package com.medals.medalsbackend.service.user;

import com.medals.medalsbackend.entity.users.Admin;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.users.Trainer;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.exception.InternalException;
import com.medals.medalsbackend.repository.UserEntityRepository;
import com.medals.medalsbackend.service.user.login.EmailAlreadyExistsException;
import com.medals.medalsbackend.service.user.login.EmailDoesntExistException;
import com.medals.medalsbackend.service.user.login.LoginEntryService;
import com.medals.medalsbackend.service.onetimecode.OneTimeCodeCreationReason;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEntityService {
  private final UserEntityRepository userEntityRepository;
  private final LoginEntryService loginEntryService;

  @Transactional
  public UserEntity save(String email, UserEntity userEntity, OneTimeCodeCreationReason reason) {
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

  public Optional<UserEntity> findByEmail(String email) { return userEntityRepository.findByEmail(email); }

  public List<UserEntity> getAll() {
    return userEntityRepository.findAll();
  }

  public List<Athlete> getAllAthletes() {
    return userEntityRepository.findAllAthletes();
  }

  public List<Trainer> getAllTrainers() {
    return userEntityRepository.findAllTrainers();
  }

  public List<Admin> getAllAdmins() {
    return userEntityRepository.findAllAdmins();
  }

  public UserEntity deleteById(Long id) {
    UserEntity userEntity = userEntityRepository.findById(id).orElseThrow();
    userEntityRepository.delete(userEntity);
    return userEntity;
  }

  public Athlete findAthleteByEmailAndBirthdate(String email, LocalDate birthdate) {
    return userEntityRepository.findAthleteByEmailAndBirthdate(email, birthdate);
  }

  public boolean existsById(Long id) {
    return userEntityRepository.existsById(id);
  }
}
