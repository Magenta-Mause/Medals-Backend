package com.medals.medalsbackend.repository;

import com.medals.medalsbackend.entity.users.Admin;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.users.Trainer;
import com.medals.medalsbackend.entity.users.UserEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
  List<UserEntity> findAll();

  Optional<UserEntity> findById(Long id);

  Optional<UserEntity> findByEmail(String email);

  @Query("SELECT u FROM Admin u")
  List<Admin> findAllAdmins();

  @Query("SELECT u FROM Athlete u")
  List<Athlete> findAllAthletes();

  @Query("SELECT u FROM Trainer u")
  List<Trainer> findAllTrainers();

  @Query("SELECT a FROM Athlete a WHERE " +
          "(LOWER(a.firstName) LIKE LOWER(CONCAT('%', :athleteSearch, '%')) " +
          "OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :athleteSearch, '%')))" +
          "OR LOWER(a.email) LIKE LOWER(CONCAT('%', :athleteSearch, '%'))")
  List<Athlete> findAllSimilarAthletes(@Param("athleteSearch") String athleteSearch);

  @Query("SELECT a FROM Athlete a WHERE " +
          "(LOWER(a.firstName) LIKE LOWER(CONCAT('%', :athleteFirstName, '%')) " +
          "OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :athleteLastName, '%')))")
  List<Athlete> findAllSimilarAthletesFullName(@Param("athleteFirstName") String athleteFirstName, @Param("athleteLastName") String athleteLastName);
}
