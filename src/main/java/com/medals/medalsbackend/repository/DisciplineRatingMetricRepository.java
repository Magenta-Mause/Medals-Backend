package com.medals.medalsbackend.repository;

import com.medals.medalsbackend.entity.performancerecording.DisciplineRatingMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface DisciplineRatingMetricRepository extends JpaRepository<DisciplineRatingMetric, Long> {

    @Query("SELECT d FROM discipline_rating_metric d WHERE d.discipline.validIn=?1 AND d.startAge<=?2 AND d.endAge>=?2")
    Collection<DisciplineRatingMetric> getAllByAge(int selectedYear, int age);

    @Query("SELECT d FROM discipline_rating_metric d WHERE d.discipline.id=?1 AND d.startAge<=?2 AND d.endAge>=?2")
    Collection<DisciplineRatingMetric> getDisciplineRatingMetricForAge(long disciplineId, int age);

    @Query("SELECT d FROM discipline_rating_metric d WHERE d.discipline.id=?1")
    Collection<DisciplineRatingMetric> getDisciplineRatingMetricByDisciplineId(long disciplineId);

    @Query("SELECT d from discipline_rating_metric d WHERE d.discipline.validIn=?1")
    Collection<DisciplineRatingMetric> getDisciplineRatingMetricBySelectedYear(int selectedYear);
}
