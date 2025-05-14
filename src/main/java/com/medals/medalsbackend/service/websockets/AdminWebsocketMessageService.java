package com.medals.medalsbackend.service.websockets;

import com.medals.medalsbackend.entity.users.Admin;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminWebsocketMessageService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendAdminDelete(Long adminId) {
        messagingTemplate.convertAndSend("/topics/admin/deletion/admin", adminId);
    }

    public void sendAdminUpdate(Admin admin) {
        messagingTemplate.convertAndSend("/topics/admin/update/admin", admin);
    }

    public void sendAdminCreate(Admin admin) {
        messagingTemplate.convertAndSend("/topics/admin/creation/admin", admin);
    }
}
