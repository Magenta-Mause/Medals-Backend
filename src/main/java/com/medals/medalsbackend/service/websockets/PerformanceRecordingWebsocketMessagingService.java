package com.medals.medalsbackend.service.websockets;

import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.entity.performancerecording.PerformanceRecording;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerformanceRecordingWebsocketMessagingService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendPerformanceRecordingCreation(PerformanceRecording performanceRecording) {
        messagingTemplate.convertAndSend("/topics/performance-recording/creation", performanceRecording);
    }

    public void sendPerformanceRecordingUpdate(PerformanceRecording performanceRecording) {
        messagingTemplate.convertAndSend("/topics/performance-recording/update", performanceRecording);
    }

    public void sendPerformanceRecordingDeletion(Long performanceRecordingId) {
        messagingTemplate.convertAndSend("/topics/performance-recording/deletion", performanceRecordingId);
    }
}
