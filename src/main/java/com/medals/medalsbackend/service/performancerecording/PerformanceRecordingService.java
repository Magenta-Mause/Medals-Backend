package com.medals.medalsbackend.service.performancerecording;

import com.medals.medalsbackend.entity.performancerecording.Discipline;
import com.medals.medalsbackend.entity.performancerecording.DisciplineRatingMetric;
import com.medals.medalsbackend.entity.performancerecording.PerformanceRecording;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.exception.AthleteNotFoundException;
import com.medals.medalsbackend.exception.performancerecording.NoMatchingDisciplineRatingFoundForAge;
import com.medals.medalsbackend.repository.PerformanceRecordingRepository;
import com.medals.medalsbackend.service.user.AthleteService;
import com.medals.medalsbackend.service.websockets.PerformanceRecordingWebsocketMessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceRecordingService {

    private final PerformanceRecordingRepository performanceRecordingRepository;
    private final DisciplineService disciplineService;
    private final PerformanceRecordingWebsocketMessagingService performanceRecordingWebsocketMessagingService;
    private final AthleteService athleteService;

    public PerformanceRecording recordPerformance(Athlete athlete, Discipline discipline, double value, Date dateOfPerformance) throws NoMatchingDisciplineRatingFoundForAge {
        int age = dateOfPerformance.getYear() + 1900 - athlete.getBirthdate().getYear();
        DisciplineRatingMetric metric = disciplineService.getDisciplineMetricForAge(discipline, age, dateOfPerformance.getYear() + 1900);
        PerformanceRecording performanceRecording = PerformanceRecording.builder()
                .ageAtRecording(age)
                .dateOfPerformance(dateOfPerformance)
                .ratingValue(value)
                .disciplineRatingMetric(metric)
                .athlete(athlete)
                .athleteId(athlete.getId())
                .build();

        performanceRecording = performanceRecordingRepository.save(performanceRecording);
        performanceRecordingWebsocketMessagingService.sendPerformanceRecordingCreation(performanceRecording);
        log.info("Recorded new Performance for athlete with id {}", athlete.getId());
        return performanceRecording;
    }

    public void deletePerformanceRecording(Long performanceRecordingId) throws PerformanceRecordingNotFoundException {
        performanceRecordingWebsocketMessagingService.sendPerformanceRecordingDeletion(getPerformanceRecording(performanceRecordingId));
        performanceRecordingRepository.deleteById(performanceRecordingId);
    }

    public void updatePerformanceRecording(long id, PerformanceRecording performanceRecording) {
        performanceRecording.setId(id);
        performanceRecordingRepository.save(performanceRecording);
        performanceRecordingWebsocketMessagingService.sendPerformanceRecordingUpdate(performanceRecording);
    }

    public Collection<PerformanceRecording> getPerformanceRecordingsForAthlete(Long athleteId) throws AthleteNotFoundException {
        athleteService.getAthlete(athleteId);
        return performanceRecordingRepository.getAllByAthleteId(athleteId);
    }

    public Collection<PerformanceRecording> getAllPerformanceRecordings() {
        return performanceRecordingRepository.findAll();
    }

    public PerformanceRecording getPerformanceRecording(Long id) throws PerformanceRecordingNotFoundException {
        return performanceRecordingRepository.findById(id).orElseThrow(() -> new PerformanceRecordingNotFoundException(id));
    }
}
