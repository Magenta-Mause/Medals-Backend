package com.medals.medalsbackend.controller.performancerecording;

import com.medals.medalsbackend.controller.BaseController;
import com.medals.medalsbackend.dto.performancerecording.PerformanceRecordingDto;
import com.medals.medalsbackend.entity.performancerecording.PerformanceRecording;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.entity.users.UserType;
import com.medals.medalsbackend.exception.AthleteNotFoundException;
import com.medals.medalsbackend.exception.performancerecording.DisciplineNotFoundException;
import com.medals.medalsbackend.exception.performancerecording.NoMatchingDisciplineRatingFoundForAge;
import com.medals.medalsbackend.service.authorization.AuthorizationService;
import com.medals.medalsbackend.service.authorization.NoAuthenticationFoundException;
import com.medals.medalsbackend.service.authorization.ForbiddenException;
import com.medals.medalsbackend.service.performancerecording.DisciplineService;
import com.medals.medalsbackend.service.performancerecording.PerformanceRecordingNotFoundException;
import com.medals.medalsbackend.service.performancerecording.PerformanceRecordingService;
import com.medals.medalsbackend.service.user.AthleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<PerformanceRecording> recordPerformance(@RequestBody PerformanceRecordingDto performanceRecordingDto) throws AthleteNotFoundException, DisciplineNotFoundException, NoMatchingDisciplineRatingFoundForAge, ForbiddenException, NoAuthenticationFoundException {
        authorizationService.checkUserHasAccess(performanceRecordingDto.getAthleteId());
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
    public ResponseEntity<Collection<PerformanceRecording>> getPerformanceRecordings() throws NoAuthenticationFoundException, AthleteNotFoundException {
        UserEntity selectedUser = authorizationService.getSelectedUser();
        return ResponseEntity.ok(switch (selectedUser.getType()) {
            case UserType.ADMIN -> performanceRecordingService.getAllPerformanceRecordings();
            case UserType.ATHLETE ->
                performanceRecordingService.getPerformanceRecordingsForAthlete(selectedUser.getId());
            case UserType.TRAINER ->
                performanceRecordingService.getAllPerformanceRecordings(); // TODO: Add assign logic here
        });
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerformanceRecording(@PathVariable Long id) throws PerformanceRecordingNotFoundException, ForbiddenException, NoAuthenticationFoundException {
        authorizationService.checkUserHasAccess(performanceRecordingService.getPerformanceRecording(id).getAthleteId());
        performanceRecordingService.deletePerformanceRecording(id);
        return ResponseEntity.noContent().build();
    }
}
