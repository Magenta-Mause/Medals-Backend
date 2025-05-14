package com.medals.medalsbackend.controller.athlete;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.dto.AthleteUpdateDto;
import com.medals.medalsbackend.entity.medals.MedalCollection;
import com.medals.medalsbackend.entity.performancerecording.PerformanceRecording;
import com.medals.medalsbackend.entity.swimCertificate.SwimCertificateType;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.users.Trainer;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.entity.users.UserType;
import com.medals.medalsbackend.exception.AthleteNotFoundException;
import com.medals.medalsbackend.exception.InternalException;
import com.medals.medalsbackend.security.jwt.JwtTokenBody;
import com.medals.medalsbackend.security.jwt.JwtUtils;
import com.medals.medalsbackend.service.authorization.AuthorizationService;
import com.medals.medalsbackend.service.authorization.NoAuthenticationFoundException;
import com.medals.medalsbackend.service.authorization.ForbiddenException;
import com.medals.medalsbackend.service.performancerecording.PerformanceRecordingService;
import com.medals.medalsbackend.exception.JwtTokenInvalidException;
import com.medals.medalsbackend.exception.TrainerNotFoundException;
import com.medals.medalsbackend.service.user.AthleteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static com.medals.medalsbackend.controller.BaseController.BASE_PATH;


@Slf4j
@RestController
@RequestMapping(BASE_PATH + "/athletes")
@RequiredArgsConstructor
public class AthleteController {
    private final AthleteService athleteService;
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;
    private final AuthorizationService authorizationService;
    private final PerformanceRecordingService performanceRecordingService;

    @GetMapping
    public ResponseEntity<AthleteDto[]> getAthletes() throws NoAuthenticationFoundException, AthleteNotFoundException, ForbiddenException {
        UserEntity selectedUser = authorizationService.getSelectedUser();
        return ResponseEntity.ok((switch (selectedUser.getType()) {
            case UserType.ADMIN -> Arrays.stream(athleteService.getAthletes());
            case UserType.ATHLETE -> Stream.of(athleteService.getAthlete(selectedUser.getId()));
            case UserType.TRAINER -> Arrays.stream(athleteService.getAthletesAssignedToTrainer(selectedUser.getId()));
        }).map(athlete -> objectMapper.convertValue(athlete, AthleteDto.class)).toArray(AthleteDto[]::new));
    }

    @PostMapping
    public ResponseEntity<AthleteDto> postAthlete(@Valid @RequestBody AthleteDto athleteDto) throws InternalException, ForbiddenException, NoAuthenticationFoundException {
        authorizationService.assertRoleIn(List.of(UserType.TRAINER, UserType.ADMIN));
        UserEntity trainer = authorizationService.getSelectedUser();
        String trainerName = String.join(" ", trainer.getFirstName(), trainer.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED).body(objectMapper.convertValue(athleteService.insertAthlete(athleteDto, trainerName), AthleteDto.class));
    }

    @PostMapping(value = "/validate")
    public ResponseEntity<List<AthleteDto>> validateAthletes(@RequestBody @Valid List<AthleteDto> athleteDtoList) {
        return ResponseEntity.ok(athleteDtoList);
    }

    @DeleteMapping("/{athleteId}")
    public ResponseEntity<Void> deleteAthlete(@PathVariable Long athleteId) throws Exception {
        authorizationService.assertUserHasAccess(athleteId);
        athleteService.deleteAthlete(athleteId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

    @PutMapping("/{athleteId}")
    public ResponseEntity<AthleteDto> updateAthlete(@PathVariable Long athleteId, @RequestBody @Valid AthleteUpdateDto athleteUpdateDto) throws Exception {
        authorizationService.assertUserHasAccess(athleteId);
        Athlete updatedAthlete = athleteService.updateAthleteNames(athleteId, athleteUpdateDto.getFirstName(), athleteUpdateDto.getLastName());
        return ResponseEntity.ok(objectMapper.convertValue(updatedAthlete, AthleteDto.class));
    }

    @GetMapping(value = "/{athleteId}")
    public ResponseEntity<AthleteDto> getAthlete(@PathVariable Long athleteId) throws AthleteNotFoundException, ForbiddenException, NoAuthenticationFoundException {
        authorizationService.assertUserHasAccess(athleteId);
        return ResponseEntity.ok(objectMapper.convertValue(athleteService.getAthlete(athleteId), AthleteDto.class));
    }

    @GetMapping(value = "/{athleteId}/medals")
    public ResponseEntity<MedalCollection> getMedals(@PathVariable Long athleteId) throws AthleteNotFoundException, ForbiddenException, NoAuthenticationFoundException {
        authorizationService.assertUserHasAccess(athleteId);
        return ResponseEntity.ok(athleteService.getAthleteMedalCollection(athleteId));
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkAthleteExists(
            @RequestParam String email,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthdate) throws NoAuthenticationFoundException, ForbiddenException {
        authorizationService.assertRoleIn(List.of(UserType.ADMIN, UserType.TRAINER));
        return ResponseEntity.ok(athleteService.existsByBirthdateAndEmail(email, birthdate));
    }

    @GetMapping("/performance-recordings/{userId}")
    public ResponseEntity<Collection<PerformanceRecording>> getPerformanceRecordings(@PathVariable Long userId) throws AthleteNotFoundException, ForbiddenException, NoAuthenticationFoundException {
        authorizationService.assertUserHasAccess(userId);
        return ResponseEntity.ok(performanceRecordingService.getPerformanceRecordingsForAthlete(userId));
    }

    @PostMapping("/approve-access")
    public ResponseEntity<String> approveTrainerAccessRequest(@RequestParam String oneTimeCode) throws JwtTokenInvalidException, AthleteNotFoundException, TrainerNotFoundException, ForbiddenException, NoAuthenticationFoundException {
        Integer athleteId = (Integer) jwtUtils.getTokenContentBody(oneTimeCode, JwtTokenBody.TokenType.REQUEST_TOKEN).get("athleteId");
        authorizationService.assertUserHasOwnerAccess(athleteId.longValue());
        athleteService.approveAccessRequest(oneTimeCode);
        return ResponseEntity.ok("Accepted the Invite");
    }

    @PostMapping("/{athleteId}/swimming-certificate")
    public ResponseEntity<AthleteDto> addSwimmingCertificate(
            @PathVariable Long athleteId,
            @RequestBody SwimCertificateType certificate
    ) throws AthleteNotFoundException, ForbiddenException, NoAuthenticationFoundException {
        authorizationService.assertUserHasAccess(athleteId);
        Athlete updatedAthlete = athleteService.updateSwimmingCertificate(athleteId, certificate);
        return ResponseEntity.ok(objectMapper.convertValue(updatedAthlete, AthleteDto.class));
    }

    @DeleteMapping("/{athleteId}/swimming-certificate")
    public ResponseEntity<AthleteDto> removeSwimmingCertificate(
            @PathVariable Long athleteId
    ) throws AthleteNotFoundException, ForbiddenException, NoAuthenticationFoundException {
        authorizationService.assertUserHasAccess(athleteId);
        Athlete updatedAthlete = athleteService.updateSwimmingCertificate(athleteId, null);
        return ResponseEntity.ok(objectMapper.convertValue(updatedAthlete, AthleteDto.class));
    }

    @GetMapping(value = "/{athleteId}/assigned-trainers")
    public ResponseEntity<List<Trainer>> getTrainerAssignedToAthlete(@PathVariable Long athleteId) throws Exception{
        authorizationService.assertUserHasAccess(athleteId);
        return ResponseEntity.ok(athleteService.getTrainerAssignedToAthlete(athleteId));

    }
}
