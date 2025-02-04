package com.medals.medalsbackend.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.entity.users.Admin;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.users.Trainer;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.repository.UserEntityRepository;
import com.medals.medalsbackend.service.user.login.EmailAlreadyExistsException;
import com.medals.medalsbackend.service.user.login.EmailDoesntExistException;
import com.medals.medalsbackend.service.user.login.LoginEntryService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEntityService {
  private final UserEntityRepository userEntityRepository;
  private final LoginEntryService loginEntryService;
  private final ObjectMapper objectMapper;

  @Transactional
  public UserEntity save(String email, UserEntity userEntity) {
    try {
      loginEntryService.createLoginEntry(email);
    } catch (EmailAlreadyExistsException ignored) {
    }

    try {
      return loginEntryService.addUserToLogin(email, userEntity);
    } catch (EmailDoesntExistException e) {
      throw new RuntimeException(e);
    }
  }

  public UserEntity update(UserEntity userEntity) {
    return userEntityRepository.save(userEntity);
  }

  @SneakyThrows
  public Optional<UserEntity> findById(long id) {
    log.info("User: {}", objectMapper.writeValueAsString(userEntityRepository.findById(id).orElse(null)));
    return userEntityRepository.findById(id);
  }

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

  public boolean existsById(Long id) {
    return userEntityRepository.existsById(id);
  }
}
