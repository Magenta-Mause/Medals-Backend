package com.medals.medalsbackend.service;

import com.medals.medalsbackend.dto.AthleteDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AthleteWebsocketMessageService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendAthleteCreation(AthleteDto athlete) {
        messagingTemplate.convertAndSend("/topics/athlete/creation", athlete);
    }

    public void sendAthleteUpdate(AthleteDto athlete) {
        messagingTemplate.convertAndSend("/topics/athlete/update", athlete);
    }

    public void sendAthleteDelete(Long athleteId) {
        messagingTemplate.convertAndSend("/topics/athlete/deletion", athleteId);
    }
}
