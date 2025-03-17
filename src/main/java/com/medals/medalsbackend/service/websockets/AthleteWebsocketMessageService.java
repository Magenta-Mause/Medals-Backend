package com.medals.medalsbackend.service.websockets;

import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.entity.users.Trainer;
import com.medals.medalsbackend.service.user.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AthleteWebsocketMessageService {

	private final SimpMessagingTemplate messagingTemplate;
	private final TrainerService trainerService;

	public void sendAthleteCreation(AthleteDto athlete) {
		messagingTemplate.convertAndSend("/topics/athlete/creation", athlete);
		for (Trainer trainer : trainerService.getAllTrainers()) {
			messagingTemplate.convertAndSend("/topics/athlete/creation/" + trainer.getId(), athlete);
		}
	}

	public void sendAthleteUpdate(AthleteDto athlete) {
		messagingTemplate.convertAndSend("/topics/athlete/update", athlete);
		for (Trainer trainer : trainerService.getAllTrainers()) {
			messagingTemplate.convertAndSend("/topics/athlete/update/" + trainer.getId(), athlete);
		}
	}

	public void sendAthleteDelete(Long athleteId) {
		messagingTemplate.convertAndSend("/topics/athlete/deletion", athleteId);
		for (Trainer trainer : trainerService.getAllTrainers()) {
			messagingTemplate.convertAndSend("/topics/athlete/deletion/" + trainer.getId(), athleteId);
		}
	}
}
