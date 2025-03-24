package com.medals.medalsbackend.repository;

import com.medals.medalsbackend.dto.PrunedAthleteDto;
import com.medals.medalsbackend.entity.users.Admin;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.users.Trainer;
import com.medals.medalsbackend.entity.users.UserEntity;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
  List<UserEntity> findAll();

  Optional<UserEntity> findById(Long id);

  @Query("SELECT u FROM Admin u")
  List<Admin> findAllAdmins();

  @Query("SELECT u FROM Athlete u")
  List<Athlete> findAllAthletes();

  @Query("SELECT u FROM Trainer u")
  List<Trainer> findAllTrainers();

  List<UserEntity> getAllByEmail(String email);

  @Query("SELECT new com.medals.medalsbackend.dto.PrunedAthleteDto(a.id, a.firstName, a.lastName, a.birthdate, a.) " +
          "FROM Athlete a " +
          "WHERE LOWER(CONCAT(a.firstName, ' ', a.lastName)) LIKE LOWER(CONCAT('%', :userInput, '%'))")
  List<PrunedAthleteDto> searchGeneric(@Param("userInput") String userInput);

  @Query("SELECT u.assignedAthletes FROM Trainer u WHERE u.id = :id")
  List<Athlete> findAthletes(Long id);
}
