package com.medals.medalsbackend.controller.performancerecording;

import com.medals.medalsbackend.controller.BaseController;
import com.medals.medalsbackend.dto.performancerecording.PerformanceRecordingDto;
import com.medals.medalsbackend.entity.performancerecording.PerformanceRecording;
import com.medals.medalsbackend.exception.AthleteNotFoundException;
import com.medals.medalsbackend.exception.performancerecording.DisciplineNotFoundException;
import com.medals.medalsbackend.exception.performancerecording.NoMatchingDisciplineRatingFoundForAge;
import com.medals.medalsbackend.service.performancerecording.DisciplineService;
import com.medals.medalsbackend.service.performancerecording.PerformanceRecordingService;
import com.medals.medalsbackend.service.user.AthleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
@RequestMapping(BaseController.BASE_PATH + "/performance-recordings")
@RequiredArgsConstructor
public class PerformanceRecordingController {

    private final PerformanceRecordingService performanceRecordingService;
    private final AthleteService athleteService;
    private final DisciplineService disciplineService;

    @PostMapping
    public ResponseEntity<PerformanceRecording> recordPerformance(@RequestBody PerformanceRecordingDto performanceRecordingDto) throws AthleteNotFoundException, DisciplineNotFoundException, NoMatchingDisciplineRatingFoundForAge {
        PerformanceRecording performanceRecording = performanceRecordingService.recordPerformance(
                athleteService.getAthlete(performanceRecordingDto.getAthleteId()),
                disciplineService.getDisciplineById(performanceRecordingDto.getDisciplineId()),
                performanceRecordingDto.getRatingValue(),
                performanceRecordingDto.getDateOfPerformance()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(performanceRecording);
    }

    @GetMapping
    public ResponseEntity<Collection<PerformanceRecording>> getPerformanceRecordings() {
        return ResponseEntity.ok(performanceRecordingService.getAllPerformanceRecordings());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Collection<PerformanceRecording>> getPerformanceRecordings(@PathVariable Long userId) throws AthleteNotFoundException {
        return ResponseEntity.ok(performanceRecordingService.getPerformanceRecordingsForAthlete(athleteService.getAthlete(userId)));
    }

}
