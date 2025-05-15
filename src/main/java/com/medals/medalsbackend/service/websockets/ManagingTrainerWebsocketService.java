package com.medals.medalsbackend.service.websockets;

import com.medals.medalsbackend.entity.users.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagingTrainerWebsocketService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendManagingTrainerCreation(long athleteId, Trainer trainer) {
        simpMessagingTemplate.convertAndSend("/topics/controlling-trainer/creation/" + athleteId, trainer);
    }

    public void sendManagingTrainerUpdate(long athleteId, Trainer trainer) {
        simpMessagingTemplate.convertAndSend("/topics/controlling-trainer/update/" + athleteId, trainer);
    }

    public void sendManagingTrainerDeletion(long athleteId, long trainerId) {
        simpMessagingTemplate.convertAndSend("/topics/controlling-trainer/deletion/" + athleteId, trainerId);
    }
}
