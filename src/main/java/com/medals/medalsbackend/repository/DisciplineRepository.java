package com.medals.medalsbackend.repository;

import com.medals.medalsbackend.entity.performancerecording.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface DisciplineRepository extends JpaRepository<Discipline, Long> {
}
