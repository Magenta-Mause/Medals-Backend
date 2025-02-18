package com.medals.medalsbackend.service.performancerecording;

import com.medals.medalsbackend.entity.performancerecording.Discipline;
import com.medals.medalsbackend.entity.performancerecording.DisciplineRatingMetric;
import com.medals.medalsbackend.entity.performancerecording.PerformanceRecording;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.exception.performancerecording.NoMatchingDisciplineRatingFoundForAge;
import com.medals.medalsbackend.repository.PerformanceRecordingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceRecordingService {

    private final PerformanceRecordingRepository performanceRecordingRepository;
    private final DisciplineService disciplineService;

    public PerformanceRecording recordPerformance(Athlete athlete, Discipline discipline, int selectedYear, double value) throws NoMatchingDisciplineRatingFoundForAge {
        int age = selectedYear - athlete.getBirthdate().getYear();
        DisciplineRatingMetric metric = disciplineService.getDisciplineMetricForAge(discipline, age);
        PerformanceRecording performanceRecording = PerformanceRecording.builder()
                .ageAtRecording(age)
                .dateRecorded(LocalDateTime.now())
                .ratingValue(value)
                .disciplineRatingMetric(metric)
                .athlete(athlete)
                .build();
        performanceRecordingRepository.save(performanceRecording);

        return performanceRecording;
    }

    public Collection<PerformanceRecording> getPerformanceRecordingsForAthlete(Athlete athlete) {
        return performanceRecordingRepository.getAllByAthleteId(athlete.getId());
    }
}
