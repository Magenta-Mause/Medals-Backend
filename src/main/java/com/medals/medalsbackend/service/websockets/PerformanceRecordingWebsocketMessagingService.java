package com.medals.medalsbackend.service.websockets;

import com.medals.medalsbackend.entity.performancerecording.PerformanceRecording;
import com.medals.medalsbackend.entity.users.Trainer;
import com.medals.medalsbackend.service.user.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerformanceRecordingWebsocketMessagingService {
	private final SimpMessagingTemplate messagingTemplate;
	private final TrainerService trainerService;

	public void sendPerformanceRecordingCreation(PerformanceRecording performanceRecording) {
		messagingTemplate.convertAndSend("/topics/performance-recording/creation/" + performanceRecording.getAthleteId(), performanceRecording);
		for (Trainer trainer : trainerService.getAllTrainers()) {
			messagingTemplate.convertAndSend("/topics/performance-recording/creation/" + trainer.getId(), performanceRecording);
		}
	}

	public void sendPerformanceRecordingUpdate(PerformanceRecording performanceRecording) {
		messagingTemplate.convertAndSend("/topics/performance-recording/update/" + performanceRecording.getAthleteId(), performanceRecording);
		for (Trainer trainer : trainerService.getAllTrainers()) {
			messagingTemplate.convertAndSend("/topics/performance-recording/update/" + trainer.getId(), performanceRecording);
		}
	}

	public void sendPerformanceRecordingDeletion(Long performanceRecordingId) {
		messagingTemplate.convertAndSend("/topics/performance-recording/deletion", performanceRecordingId);
		for (Trainer trainer : trainerService.getAllTrainers()) {
			messagingTemplate.convertAndSend("/topics/performance-recording/deletion/" + trainer.getId(), performanceRecordingId);
		}
	}
}
