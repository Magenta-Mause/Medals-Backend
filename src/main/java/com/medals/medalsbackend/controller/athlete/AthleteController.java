package com.medals.medalsbackend.controller.athlete;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.entity.medals.MedalCollection;
import com.medals.medalsbackend.entity.performancerecording.PerformanceRecording;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            case UserType.TRAINER -> {
                authorizationService.assertUserHasOwnerAccess(selectedUser.getId());
                yield Arrays.stream(athleteService.getAthletesFromTrainer(selectedUser.getId()));}
        }).map(athlete -> objectMapper.convertValue(athlete, AthleteDto.class)).toArray(AthleteDto[]::new));
    }

    @PostMapping
    public ResponseEntity<AthleteDto> postAthlete(@Valid @RequestBody AthleteDto athleteDto) throws InternalException, ForbiddenException, NoAuthenticationFoundException {
        authorizationService.assertRoleIn(List.of(UserType.TRAINER, UserType.ADMIN));
        return ResponseEntity.status(HttpStatus.CREATED).body(objectMapper.convertValue(athleteService.insertAthlete(athleteDto), AthleteDto.class));
    }

    @PostMapping(value = "/validate")
    public ResponseEntity<List<AthleteDto>> validateAthletes(@RequestBody @Valid List<AthleteDto> athleteDtoList) {
        return ResponseEntity.ok(athleteDtoList);
    }

    @DeleteMapping("/{athleteId}")
    public ResponseEntity<Void> deleteAthlete(@PathVariable Long athleteId) throws AthleteNotFoundException, NoAuthenticationFoundException, ForbiddenException {
        authorizationService.assertUserHasAccess(athleteId);
        athleteService.deleteAthlete(athleteId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
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

    @GetMapping(value = "/{athleteId}/swimmingCertificate")
    public ResponseEntity<Boolean> getSwimmingCertificate(@PathVariable Long athleteId) throws AthleteNotFoundException, ForbiddenException, NoAuthenticationFoundException {
        authorizationService.assertUserHasAccess(athleteId);
        return ResponseEntity.ok(athleteService.getAthlete(athleteId).isSwimmingCertificate());
    }

    @GetMapping("/performance-recordings/{userId}")
    public ResponseEntity<Collection<PerformanceRecording>> getPerformanceRecordings(@PathVariable Long userId) throws AthleteNotFoundException, ForbiddenException, NoAuthenticationFoundException {
        authorizationService.assertUserHasAccess(userId);
        return ResponseEntity.ok(performanceRecordingService.getPerformanceRecordingsForAthlete(userId));
    }

    @PostMapping("/approve-access")
    public ResponseEntity<String> approveTrainerAccessRequest(@RequestParam String oneTimeCode) throws JwtTokenInvalidException, AthleteNotFoundException, TrainerNotFoundException, ForbiddenException, NoAuthenticationFoundException {
        Integer athleteId = (Integer) jwtUtils.getTokenContentBody(oneTimeCode, JwtTokenBody.TokenType.REQUEST_TOKEN).get("athleteId");
        authorizationService.assertUserHasAccess(athleteId.longValue());
        athleteService.approveAccessRequest(oneTimeCode);
        return ResponseEntity.ok("Accepted the Invite");
    }
}
