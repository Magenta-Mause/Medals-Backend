package com.medals.medalsbackend.repository;

import com.medals.medalsbackend.entity.performancerecording.DisciplineRatingMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface DisciplineRatingMetricRepository extends JpaRepository<DisciplineRatingMetric, Long> {

    @Query("SELECT d FROM discipline_rating_metric d WHERE d.validIn=?1 AND d.startAge<=?2 AND d.endAge>=?2")
    Collection<DisciplineRatingMetric> getAllByAge(int selectedYear, int age);

    @Query("SELECT d FROM discipline_rating_metric d WHERE d.discipline.id=?1 AND d.startAge<=?2 AND d.endAge>=?2 AND d.validIn = ?3")
    Collection<DisciplineRatingMetric> getDisciplineRatingMetricForAge(long disciplineId, int age, int selectedYear);

    @Query("SELECT d FROM discipline_rating_metric d WHERE d.discipline.id=?1")
    Collection<DisciplineRatingMetric> getDisciplineRatingMetricByDisciplineId(long disciplineId);

    @Query("SELECT d FROM discipline_rating_metric d")
    Collection<DisciplineRatingMetric> getAllDisciplineRatingMetrics();
}
