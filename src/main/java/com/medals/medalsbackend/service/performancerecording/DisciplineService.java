package com.medals.medalsbackend.service.performancerecording;

import com.medals.medalsbackend.DummyData;
import com.medals.medalsbackend.entity.performancerecording.Discipline;
import com.medals.medalsbackend.entity.performancerecording.DisciplineRatingMetric;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.exception.performancerecording.DisciplineNotFoundException;
import com.medals.medalsbackend.exception.performancerecording.NoMatchingDisciplineRatingFoundForAge;
import com.medals.medalsbackend.repository.DisciplineRatingMetricRepository;
import com.medals.medalsbackend.repository.DisciplineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisciplineService {
    private final DisciplineRatingMetricRepository disciplineRatingMetricRepository;
    private final DisciplineRepository disciplineRepository;
    @Value("${app.dummies.enabled}")
    private boolean insertDummies;

    @EventListener(ApplicationReadyEvent.class)
    @Profile("!test")
    public void instantiateDummys() {
        if (!insertDummies) {
            return;
        }

        log.info("Inserting dummy data...");
        Collection<DisciplineRatingMetric> disciplineRatingMetrics = DummyData.getDisciplineRatingMetric();
        Set<Discipline> disciplines = new LinkedHashSet<>(disciplineRatingMetrics.stream().map(DisciplineRatingMetric::getDiscipline).toList());
        disciplines.forEach(this::insertDiscipline);
        disciplineRatingMetrics.forEach(metric -> {
            try {
                insertDisciplineRatingMetric(metric);
            } catch (DisciplineNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        log.info("Inserted dummy data...");
    }

    public Discipline insertDiscipline(Discipline discipline) {
        discipline.setId(null);
        log.info("Inserting discipline {}", discipline);
        return disciplineRepository.save(discipline);
    }

    public DisciplineRatingMetric insertDisciplineRatingMetric(long disciplineId, DisciplineRatingMetric disciplineRatingMetric) throws DisciplineNotFoundException {
        disciplineRatingMetric.setId(null);
        disciplineRatingMetric.setDiscipline(getDisciplineById(disciplineId));
        return disciplineRatingMetricRepository.save(disciplineRatingMetric);
    }

    public DisciplineRatingMetric insertDisciplineRatingMetric(DisciplineRatingMetric disciplineRatingMetric) throws DisciplineNotFoundException {
        disciplineRatingMetric.setId(null);
        return disciplineRatingMetricRepository.save(disciplineRatingMetric);
    }

    public Collection<DisciplineRatingMetric> getDisciplineRatingMetricsForAthlete(Athlete athlete, int selectedYear) {
        int age = selectedYear - athlete.getBirthdate().getYear();
        return disciplineRatingMetricRepository.getAllByAge(selectedYear, age);
    }

    public DisciplineRatingMetric getDisciplineMetricForAge(Discipline discipline, int age) throws NoMatchingDisciplineRatingFoundForAge {
        return disciplineRatingMetricRepository.getDisciplineRatingMetricForAge(discipline.getId(), age).stream().findFirst().orElseThrow(() -> new NoMatchingDisciplineRatingFoundForAge(discipline.getId()));
    }

    public Discipline getDisciplineById(long id) throws DisciplineNotFoundException {
        return disciplineRepository.findById(id).orElseThrow(() -> new DisciplineNotFoundException(id));
    }

    public Collection<Discipline> getDisciplinesForSelectedYear(int selectedYear) {
        return disciplineRepository.findByValidIn(selectedYear);
    }

    public Collection<DisciplineRatingMetric> getDisciplineRatings(long disciplineById) {
        return disciplineRatingMetricRepository.getDisciplineRatingMetricByDisciplineId(disciplineById);
    }

    public Collection<DisciplineRatingMetric> getDisciplineRatingsForSelectedYear(int selectedYear) {
        return disciplineRatingMetricRepository.getDisciplineRatingMetricBySelectedYear(selectedYear);
    }
}
