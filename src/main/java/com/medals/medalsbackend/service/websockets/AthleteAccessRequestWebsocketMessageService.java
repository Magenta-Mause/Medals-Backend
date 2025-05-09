package com.medals.medalsbackend.service.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.controller.athlete.AthleteAccessRequestDto;
import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.users.AthleteAccessRequest;
import com.medals.medalsbackend.entity.users.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AthleteAccessRequestWebsocketMessageService {
    private final SimpMessagingTemplate messagingTemplate;
    private final AthleteWebsocketMessageService athleteWebsocketMessageService;
    private final ObjectMapper objectMapper;

    public void sendAccessRequestCreation(AthleteAccessRequestDto athleteAccessRequest) {
        messagingTemplate.convertAndSend("/topics/athlete-access-request/creation", athleteAccessRequest);
        athleteWebsocketMessageService.sendAthleteAssign(athleteAccessRequest.athlete(), athleteAccessRequest.trainer().getId());
    }

    public void sendAccessRequestAcceptance(Athlete athlete, Trainer trainer, String athleteAccessRequestId) {
        messagingTemplate.convertAndSend("/topics/athlete-access-request/deletion/" + athlete.getId(), athleteAccessRequestId);
        messagingTemplate.convertAndSend("/topics/athlete-access-request/deletion/" + trainer.getId(), athleteAccessRequestId);
        athleteWebsocketMessageService.sendAthleteAssign(objectMapper.convertValue(athlete, AthleteDto.class), trainer.getId());
    }

    public void sendAccessRequestRejection(AthleteAccessRequest athleteAccessRequest) {
        messagingTemplate.convertAndSend("/topics/athlete-access-request/deletion/" + athleteAccessRequest.trainerId, athleteAccessRequest.getId());
        messagingTemplate.convertAndSend("/topics/athlete-access-request/deletion/" + athleteAccessRequest.athleteId, athleteAccessRequest.getId());
    }
}
