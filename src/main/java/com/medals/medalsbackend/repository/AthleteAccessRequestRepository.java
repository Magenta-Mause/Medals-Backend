package com.medals.medalsbackend.repository;

import com.medals.medalsbackend.entity.users.AthleteAccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface AthleteAccessRequestRepository extends JpaRepository<AthleteAccessRequest, String> {
    Collection<AthleteAccessRequest> findAllByAthleteId(long athleteId);

    Collection<AthleteAccessRequest> findAllByTrainerId(long trainerId);

    boolean existsByAthleteIdAndTrainerId(long athleteId, long trainerId);
}
