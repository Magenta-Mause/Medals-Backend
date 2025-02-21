package com.medals.medalsbackend.service.websockets;

import com.medals.medalsbackend.entity.performancerecording.Discipline;
import com.medals.medalsbackend.entity.performancerecording.DisciplineRatingMetric;
import com.medals.medalsbackend.entity.performancerecording.PerformanceRecording;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DisciplineWebsocketMessagingService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendDisciplineCreation(Discipline discipline) {
        messagingTemplate.convertAndSend("/topics/discipline/creation", discipline);
    }

    public void sendDisciplineUpdate(Discipline discipline) {
        messagingTemplate.convertAndSend("/topics/discipline/update", discipline);
    }

    public void sendDisciplineDeletion(Long disciplineId) {
        messagingTemplate.convertAndSend("/topics/discipline/deletion", disciplineId);
    }


}
