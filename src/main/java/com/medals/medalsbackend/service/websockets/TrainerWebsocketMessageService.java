package com.medals.medalsbackend.service.websockets;

import com.medals.medalsbackend.dto.TrainerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerWebsocketMessageService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendTrainerCreation(TrainerDto trainer) {
        messagingTemplate.convertAndSend("/topics/trainer/creation/admin", trainer);
    }

    public void sendTrainerUpdate(TrainerDto trainer) {
        messagingTemplate.convertAndSend("/topics/trainer/update/admin", trainer);
    }

    public void sendTrainerDelete(Long trainerId) {
        messagingTemplate.convertAndSend("/topics/trainer/deletion/admin", trainerId);
    }
}
