package com.medals.medalsbackend.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.controller.athlete.AthleteAccessRequestDto;
import com.medals.medalsbackend.dto.TrainerDto;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.users.AthleteAccessRequest;
import com.medals.medalsbackend.entity.users.Trainer;
import com.medals.medalsbackend.entity.users.UserType;
import com.medals.medalsbackend.exception.AthleteAccessRequestNotFoundException;
import com.medals.medalsbackend.exception.AthleteAlreadyRequestedException;
import com.medals.medalsbackend.exception.AthleteNotFoundException;
import com.medals.medalsbackend.exception.TrainerNotFoundException;
import com.medals.medalsbackend.repository.AthleteAccessRequestRepository;
import com.medals.medalsbackend.service.notifications.NotificationService;
import com.medals.medalsbackend.service.websockets.AthleteAccessRequestWebsocketMessageService;
import com.medals.medalsbackend.service.websockets.AthleteWebsocketMessageService;
import com.medals.medalsbackend.service.websockets.ManagingTrainerWebsocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessRequestService {

    private final AthleteAccessRequestRepository athleteAccessRequestRepository;
    private final UserEntityService userEntityService;
    private final AthleteService athleteService;
    private final TrainerService trainerService;
    private final AthleteWebsocketMessageService athleteWebsocketMessageService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final AthleteAccessRequestWebsocketMessageService athleteAccessRequestWebsocketMessageService;
    private final ManagingTrainerWebsocketService managingTrainerWebsocketService;

    public AthleteAccessRequest getAthleteAccessRequest(String id) throws AthleteAccessRequestNotFoundException {
        return athleteAccessRequestRepository.findById(id).orElseThrow(() -> new AthleteAccessRequestNotFoundException(id));
    }

    public void cleanupAccessRequests() {
        AthleteAccessRequest[] accessRequests = athleteAccessRequestRepository.findAll().toArray(new AthleteAccessRequest[0]);
        Arrays.stream(accessRequests).forEach(accessRequest -> {
            if (!athleteService.checkExistence(accessRequest.athleteId) || !trainerService.checkExistence(accessRequest.trainerId)) {
                athleteAccessRequestRepository.deleteById(accessRequest.getId());
            }
        });
    }

    public AthleteAccessRequestDto convertAthleteAccessRequest(AthleteAccessRequest athleteAccessRequest) {
        try {
            return AthleteAccessRequestDto.builder()
                .id(athleteAccessRequest.id)
                .athlete(athleteService.truncateAthlete(athleteService.getAthlete(athleteAccessRequest.getAthleteId())))
                .trainer(objectMapper.convertValue(trainerService.getTrainer(athleteAccessRequest.trainerId), TrainerDto.class))
                .build();
        } catch (AthleteNotFoundException | TrainerNotFoundException e) {
            return null;
        }
    }

    public void initiateAthleteAccessRequest(long athleteId, long trainerId) throws Exception {
        userEntityService.assertUserType(athleteId, UserType.ATHLETE, AthleteNotFoundException.fromAthleteId(athleteId));
        userEntityService.assertUserType(trainerId, UserType.TRAINER, TrainerNotFoundException.fromTrainerId(trainerId));

        Athlete requestedAthlete = (Athlete) userEntityService.findById(athleteId).orElseThrow(() -> AthleteNotFoundException.fromAthleteId(athleteId));
        Trainer trainer = (Trainer) userEntityService.findById(trainerId).orElseThrow(() -> TrainerNotFoundException.fromTrainerId(trainerId));

        if (
            athleteAccessRequestRepository.existsByAthleteIdAndTrainerId(athleteId, trainerId)
                || trainer.getAssignedAthletes().stream().anyMatch(athlete -> athlete.getId().equals(athleteId))
        ) {
            throw new AthleteAlreadyRequestedException();
        }

        log.info("Sending request to manage athlete {} from trainer {}", requestedAthlete, trainer);

        String trainerName = trainer.getFirstName() + " " + trainer.getLastName();
        AthleteAccessRequest accessRequest = athleteAccessRequestRepository.save(AthleteAccessRequest.builder()
            .athleteId(athleteId)
            .trainerId(trainerId)
            .build());

        notificationService.sendRequestAthleteNotification(requestedAthlete.getEmail(), accessRequest.getId(), trainerName);
        athleteAccessRequestWebsocketMessageService.sendAccessRequestCreation(convertAthleteAccessRequest(accessRequest));
    }

    public Collection<AthleteAccessRequest> getAllAthleteAccessRequests() {
        cleanupAccessRequests();
        return athleteAccessRequestRepository.findAll();
    }

    public Collection<AthleteAccessRequest> getAthleteAccessRequestsOfAthlete(long athleteId) {
        cleanupAccessRequests();
        return athleteAccessRequestRepository.findAllByAthleteId(athleteId);
    }

    public Collection<AthleteAccessRequest> getAthleteAccessRequestsOfTrainer(long trainerId) {
        cleanupAccessRequests();
        return athleteAccessRequestRepository.findAllByTrainerId(trainerId);
    }

    public void revokeAccessRequest(String athleteAccessRequestId) throws AthleteAccessRequestNotFoundException {
        AthleteAccessRequest athleteAccessRequest = getAthleteAccessRequest(athleteAccessRequestId);
        athleteAccessRequestRepository.deleteById(athleteAccessRequestId);
        athleteWebsocketMessageService.sendAthleteRemoveConnection(athleteAccessRequest.getAthleteId(), athleteAccessRequest.getTrainerId());
        athleteAccessRequestWebsocketMessageService.sendAccessRequestRejection(athleteAccessRequest);
    }

    public void approveAccessRequest(AthleteAccessRequest athleteAccessRequest) throws AthleteNotFoundException, TrainerNotFoundException {
        Athlete athlete = athleteService.getAthlete(athleteAccessRequest.getAthleteId());
        Trainer trainer = trainerService.getTrainer(athleteAccessRequest.getTrainerId());
        athlete.getTrainersAssignedTo().add(trainer);
        trainer.getAssignedAthletes().add(athlete);
        userEntityService.update(athlete);
        userEntityService.update(trainer);
        athleteAccessRequestRepository.deleteById(athleteAccessRequest.getId());
        athleteAccessRequestWebsocketMessageService.sendAccessRequestAcceptance(athlete, trainer, athleteAccessRequest.getId());
        managingTrainerWebsocketService.sendManagingTrainerCreation(athlete.getId(), trainer);
    }
}
