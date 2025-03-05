package com.medals.medalsbackend.service.performancerecording;

import com.medals.medalsbackend.DummyData;
import com.medals.medalsbackend.entity.medals.InitializedEntity;
import com.medals.medalsbackend.entity.medals.InitializedEntityType;
import com.medals.medalsbackend.entity.performancerecording.Discipline;
import com.medals.medalsbackend.entity.performancerecording.DisciplineRatingMetric;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.exception.performancerecording.DisciplineNotFoundException;
import com.medals.medalsbackend.exception.performancerecording.NoMatchingDisciplineRatingFoundForAge;
import com.medals.medalsbackend.repository.DisciplineRatingMetricRepository;
import com.medals.medalsbackend.repository.DisciplineRepository;
import com.medals.medalsbackend.repository.InitializedEntityRepository;
import com.medals.medalsbackend.service.websockets.DisciplineWebsocketMessagingService;
import com.medals.medalsbackend.service.websockets.RatingMetricWebsocketMessageService;
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
    private final DisciplineWebsocketMessagingService disciplineWebsocketMessagingService;
    private final RatingMetricWebsocketMessageService ratingMetricWebsocketMessageService;
    private final InitializedEntityRepository initializedEntityRepository;
    @Value("${app.dummies.enabled}")
    private boolean insertDummies;

    @EventListener(ApplicationReadyEvent.class)
    @Profile("!test")
    public void instantiateDummies() {
        if (!insertDummies) {
            return;
        }
        if (initializedEntityRepository.existsById(InitializedEntityType.Discipline)) {
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
        initializedEntityRepository.save(new InitializedEntity(InitializedEntityType.Discipline));

        log.info("Inserted dummy data...");
    }

    public Discipline insertDiscipline(Discipline discipline) {
        discipline.setId(null);
        log.info("Inserting discipline {}", discipline);
        Discipline inserted = disciplineRepository.save(discipline);
        disciplineWebsocketMessagingService.sendDisciplineCreation(discipline);
        return inserted;
    }

    public void deleteDiscipline(long disciplineId) {
        disciplineRepository.deleteById(disciplineId);
        disciplineWebsocketMessagingService.sendDisciplineDeletion(disciplineId);
    }

    public void updateDiscipline(long id, Discipline discipline) {
        discipline.setId(id);
        Discipline updated = disciplineRepository.save(discipline);
        disciplineWebsocketMessagingService.sendDisciplineUpdate(updated);
    }

    public DisciplineRatingMetric insertDisciplineRatingMetric(long disciplineId, DisciplineRatingMetric disciplineRatingMetric) throws DisciplineNotFoundException {
        disciplineRatingMetric.setId(null);
        disciplineRatingMetric.setDiscipline(getDisciplineById(disciplineId));
        DisciplineRatingMetric inserted = disciplineRatingMetricRepository.save(disciplineRatingMetric);
        ratingMetricWebsocketMessageService.sendRatingMetricCreation(inserted);
        return inserted;
    }

    public DisciplineRatingMetric insertDisciplineRatingMetric(DisciplineRatingMetric disciplineRatingMetric) throws DisciplineNotFoundException {
        disciplineRatingMetric.setId(null);
        DisciplineRatingMetric inserted = disciplineRatingMetricRepository.save(disciplineRatingMetric);
        ratingMetricWebsocketMessageService.sendRatingMetricCreation(inserted);
        return inserted;
    }

    public void deleteDisciplineRatingMetric(Long id) {
        disciplineRatingMetricRepository.deleteById(id);
        disciplineWebsocketMessagingService.sendDisciplineDeletion(id);
    }

    public DisciplineRatingMetric updateDisciplineRatingMetric(Long id, Long disciplineId, DisciplineRatingMetric disciplineRatingMetric) throws DisciplineNotFoundException {
        disciplineRatingMetric.setId(id);
        disciplineRatingMetric.setDiscipline(getDisciplineById(disciplineId));
        DisciplineRatingMetric updated = disciplineRatingMetricRepository.save(disciplineRatingMetric);
        ratingMetricWebsocketMessageService.sendRatingMetricUpdate(updated);
        return updated;
    }

    public Collection<DisciplineRatingMetric> getDisciplineRatingMetricsForAthlete(Athlete athlete, int selectedYear) {
        int age = selectedYear - athlete.getBirthdate().getYear();
        return disciplineRatingMetricRepository.getAllByAge(selectedYear, age);
    }

    public DisciplineRatingMetric getDisciplineMetricForAge(Discipline discipline, int age, int selectedYear) throws NoMatchingDisciplineRatingFoundForAge {
        return disciplineRatingMetricRepository.getDisciplineRatingMetricForAge(discipline.getId(), age, selectedYear).stream().findFirst().orElseThrow(() -> new NoMatchingDisciplineRatingFoundForAge(discipline.getId()));
    }

    public Discipline getDisciplineById(long id) throws DisciplineNotFoundException {
        return disciplineRepository.findById(id).orElseThrow(() -> new DisciplineNotFoundException(id));
    }

    public Collection<Discipline> getDisciplinesForSelectedYear() {
        return disciplineRepository.findAll();
    }

    public Collection<DisciplineRatingMetric> getDisciplineRatings(long disciplineById) {
        return disciplineRatingMetricRepository.getDisciplineRatingMetricByDisciplineId(disciplineById);
    }

    public Collection<DisciplineRatingMetric> getDisciplineRatingsForSelectedYear(int selectedYear) {
        return disciplineRatingMetricRepository.getDisciplineRatingMetricBySelectedYear(selectedYear);
    }

    public Collection<Discipline> getDisciplines() {
        return disciplineRepository.findAll();
    }
}
