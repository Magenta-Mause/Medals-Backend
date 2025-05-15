package com.medals.medalsbackend.service.websockets;

import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.entity.users.Trainer;
import com.medals.medalsbackend.service.user.TrainerService;
import com.medals.medalsbackend.service.user.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AthleteWebsocketMessageService {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserEntityService userEntityService;

    public void sendAthleteCreation(AthleteDto athlete) {
        messagingTemplate.convertAndSend("/topics/athlete/creation", athlete);
        messagingTemplate.convertAndSend("/topics/athlete/creation/" + athlete.getId(), athlete);
        for (Trainer trainer : userEntityService.getAllTrainersAssignedToAthlete(athlete.getId())) {
            messagingTemplate.convertAndSend("/topics/athlete/creation/" + trainer.getId(), athlete);
        }
    }

    public void sendAthleteUpdate(AthleteDto athlete) {
        messagingTemplate.convertAndSend("/topics/athlete/update", athlete);
        messagingTemplate.convertAndSend("/topics/athlete/update/" + athlete.getId(), athlete);
        for (Trainer trainer : userEntityService.getAllTrainersAssignedToAthlete(athlete.getId())) {
            messagingTemplate.convertAndSend("/topics/athlete/update/" + trainer.getId(), athlete);
        }
    }

    public void sendAthleteDelete(Long athleteId) {
        messagingTemplate.convertAndSend("/topics/athlete/deletion", athleteId);
        messagingTemplate.convertAndSend("/topics/athlete/deletion/" + athleteId, athleteId);
        for (Trainer trainer : userEntityService.getAllTrainersAssignedToAthlete(athleteId)) {
            messagingTemplate.convertAndSend("/topics/athlete/deletion/" + trainer.getId(), athleteId);
        }
    }

    public void sendAthleteAssign(AthleteDto athlete, long trainerId) {
        messagingTemplate.convertAndSend("/topics/athlete/creation/" + trainerId, athlete);
    }

    public void sendAthleteRemoveConnection(Long athleteId, Long trainerId) {
        messagingTemplate.convertAndSend("/topics/athlete/deletion/" + trainerId, athleteId);
    }
}
