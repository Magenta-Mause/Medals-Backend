package com.medals.medalsbackend.repository;

import com.medals.medalsbackend.entity.Athlete;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AthleteRepository extends CrudRepository<Athlete, Long> {
}
