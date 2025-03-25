package com.medals.medalsbackend.controller.performancerecording;

import com.medals.medalsbackend.controller.BaseController;
import com.medals.medalsbackend.entity.performancerecording.Discipline;
import com.medals.medalsbackend.entity.performancerecording.DisciplineRatingMetric;
import com.medals.medalsbackend.entity.users.UserType;
import com.medals.medalsbackend.exception.AthleteNotFoundException;
import com.medals.medalsbackend.exception.performancerecording.DisciplineNotFoundException;
import com.medals.medalsbackend.service.authorization.AuthorizationService;
import com.medals.medalsbackend.service.authorization.ForbiddenException;
import com.medals.medalsbackend.service.authorization.NoAuthenticationFoundException;
import com.medals.medalsbackend.service.performancerecording.DisciplineService;
import com.medals.medalsbackend.service.user.AthleteService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(BaseController.BASE_PATH + "/disciplines")
@RequiredArgsConstructor
public class DisciplineController {
    private final DisciplineService disciplineService;
    private final AthleteService athleteService;
    private final AuthorizationService authorizationService;

    @GetMapping
    public ResponseEntity<Collection<Discipline>> getDisciplines() {
        return ResponseEntity.ok(disciplineService.getDisciplines());
    }

    @PostMapping
    public ResponseEntity<Discipline> insertDiscipline(@RequestBody Discipline discipline) throws ForbiddenException, NoAuthenticationFoundException {
        authorizationService.assertRoleIn(List.of(UserType.TRAINER, UserType.ADMIN));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(disciplineService.insertDiscipline(discipline));
    }

    @GetMapping("/metrics")
    public ResponseEntity<Collection<DisciplineRatingMetric>> getAllDisciplineMetrics() {
        return ResponseEntity.ok(disciplineService.getAllDisciplineRatings());
    }

    @PostMapping("/metrics/{disciplineId}")
    public ResponseEntity<DisciplineRatingMetric> insertDisciplineRatingMetric(@PathVariable Long disciplineId, @RequestBody DisciplineRatingMetric ratingMetric) throws DisciplineNotFoundException, ForbiddenException, NoAuthenticationFoundException {
        authorizationService.assertRoleIn(List.of(UserType.TRAINER, UserType.ADMIN));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(disciplineService.insertDisciplineRatingMetric(disciplineId, ratingMetric));
    }

    @GetMapping("/metrics/{disciplineId}")
    public ResponseEntity<Collection<DisciplineRatingMetric>> getMetricsForDiscipline(@PathVariable Long disciplineId) {
        return ResponseEntity.ok(disciplineService.getDisciplineRatings(disciplineId));
    }

    @GetMapping("/athletes/{athleteId}")
    public ResponseEntity<Collection<DisciplineRatingMetric>> getViableMetricsForAthlete(@PathVariable Long athleteId, @PathParam("selected_year") int selectedYear) throws AthleteNotFoundException, ForbiddenException, NoAuthenticationFoundException {
        authorizationService.assertUserHasAccess(athleteId);
        return ResponseEntity.ok(disciplineService.getDisciplineRatingMetricsForAthlete(athleteService.getAthlete(athleteId), selectedYear));
    }
}
