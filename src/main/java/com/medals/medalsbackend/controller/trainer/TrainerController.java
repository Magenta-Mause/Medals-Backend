package com.medals.medalsbackend.controller.trainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.controller.athlete.AthleteAccessRequestDto;
import com.medals.medalsbackend.dto.PrunedAthleteDto;
import com.medals.medalsbackend.dto.TrainerDto;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.users.AthleteAccessRequest;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.entity.users.UserType;
import com.medals.medalsbackend.exception.InternalException;
import com.medals.medalsbackend.exception.TrainerNotFoundException;
import com.medals.medalsbackend.service.authorization.AuthorizationService;
import com.medals.medalsbackend.service.authorization.ForbiddenException;
import com.medals.medalsbackend.service.authorization.NoAuthenticationFoundException;
import com.medals.medalsbackend.service.user.AccessRequestService;
import com.medals.medalsbackend.service.user.AthleteService;
import com.medals.medalsbackend.service.user.TrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.medals.medalsbackend.controller.BaseController.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH + "/trainers")
@RequiredArgsConstructor
public class TrainerController {
    private final TrainerService trainerService;
    private final AthleteService athleteService;
    private final ObjectMapper objectMapper;
    private final AuthorizationService authorizationService;
    private final AccessRequestService accessRequestService;

    @GetMapping
    public ResponseEntity<TrainerDto[]> getTrainers() throws ForbiddenException, NoAuthenticationFoundException, TrainerNotFoundException {
        authorizationService.assertRoleIn(List.of(UserType.ADMIN, UserType.TRAINER));
        return ResponseEntity.ok((
            switch (authorizationService.getSelectedUser().getType()) {
                case UserType.ADMIN -> trainerService.getAllTrainers();
                case UserType.TRAINER ->
                    List.of(trainerService.getTrainer(authorizationService.getSelectedUser().getId()));
                default ->
                    throw new IllegalStateException("Unexpected value: " + authorizationService.getSelectedUser().getType());
            }).stream().map(trainer -> objectMapper.convertValue(trainer, TrainerDto.class)).toArray(TrainerDto[]::new)
        );
    }

    @PostMapping
    public ResponseEntity<TrainerDto> postTrainer(@Valid @RequestBody TrainerDto trainerDto) throws InternalException, ForbiddenException, NoAuthenticationFoundException {
        authorizationService.assertRoleIn(List.of(UserType.ADMIN));
        UserEntity admin = authorizationService.getSelectedUser();
        String adminName = String.join(" ", admin.getFirstName(), admin.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED).body(objectMapper.convertValue(trainerService.insertTrainer(trainerDto, adminName), TrainerDto.class));
    }

    @PostMapping(value = "/validate")
    public ResponseEntity<List<TrainerDto>> validateTrainers(@RequestBody @Valid List<TrainerDto> trainerDtoList) {
        return ResponseEntity.ok(trainerDtoList);
    }

    @DeleteMapping("/{trainerId}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long trainerId) throws Throwable {
        authorizationService.assertUserHasOwnerAccess(trainerId);
        trainerService.deleteTrainer(trainerId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

    @GetMapping("/{trainerId}")
    public ResponseEntity<TrainerDto> getTrainer(@PathVariable Long trainerId) throws TrainerNotFoundException, ForbiddenException, NoAuthenticationFoundException {
        authorizationService.assertUserHasOwnerAccess(trainerId);
        return ResponseEntity.ok(objectMapper.convertValue(trainerService.getTrainer(trainerId), TrainerDto.class));
    }

    @PostMapping("/access-request/{userId}")
    public ResponseEntity<Void> requestAthleteAccess(@PathVariable long userId) throws Exception {
        authorizationService.assertRoleIn(List.of(UserType.TRAINER));
        accessRequestService.initiateAthleteAccessRequest(userId, authorizationService.getSelectedUser().getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/access-requests")
    public ResponseEntity<Collection<AthleteAccessRequestDto>> getAccessRequests() throws ForbiddenException, NoAuthenticationFoundException {
        authorizationService.assertRoleIn(List.of(UserType.TRAINER));
        long userId = authorizationService.getSelectedUser().getId();
        return ResponseEntity.ok(accessRequestService.getAthleteAccessRequestsOfTrainer(userId).stream()
            .map(accessRequestService::convertAthleteAccessRequest)
            .filter(request -> !Objects.isNull(request))
            .collect(java.util.stream.Collectors.toList()));
    }

    @GetMapping(value = "/search-athletes")
    public ResponseEntity<List<PrunedAthleteDto>> searchAthletes(@RequestParam String athleteSearch) throws NoAuthenticationFoundException {
        authorizationService.getSelectedUser();
        List<Athlete> athletes = trainerService.searchAthletes(athleteSearch);
        List<PrunedAthleteDto> prunedAthletes = athletes.stream()
            .map(athlete -> objectMapper.convertValue(athlete, PrunedAthleteDto.class))
            .toList();
        return ResponseEntity.ok(prunedAthletes);
    }

    @DeleteMapping(value = "/trainer-athlete-connection")
    public ResponseEntity<Void> removeTrainerAthleteConnection(@RequestParam Long trainerId, @RequestParam Long athleteId) throws Exception {
        authorizationService.assertUserHasOwnerAccess(trainerId);
        try {
            athleteService.removeConnection(trainerId, athleteId);
        } catch (Exception ignored) {

        }
        try {
            AthleteAccessRequest athleteRequest = accessRequestService.
                getAthleteAccessRequestsOfTrainer(trainerId).stream().filter(request -> request.getAthleteId() == athleteId).findFirst().orElseThrow();
            accessRequestService.revokeAccessRequest(athleteRequest.getId());
        } catch (Exception ignored) {

        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }
}
