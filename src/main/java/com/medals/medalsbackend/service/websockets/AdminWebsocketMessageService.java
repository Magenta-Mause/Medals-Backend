package com.medals.medalsbackend.service.websockets;


import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminWebsocketMessageService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendAdminDelete(Long adminId){
        messagingTemplate.convertAndSend("/topics/admin/deletion", adminId);
    }
}
