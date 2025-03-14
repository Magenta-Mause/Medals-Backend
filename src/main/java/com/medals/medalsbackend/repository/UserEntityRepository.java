package com.medals.medalsbackend.repository;

import com.medals.medalsbackend.entity.users.Admin;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.users.Trainer;
import com.medals.medalsbackend.entity.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Date;
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

    @Query("SELECT u FROM Athlete u WHERE u.email=?1 AND u.birthdate=?2")
    Optional<Athlete> findAthleteByEmailAndBirthdate(String email, LocalDate birthdate);
}
