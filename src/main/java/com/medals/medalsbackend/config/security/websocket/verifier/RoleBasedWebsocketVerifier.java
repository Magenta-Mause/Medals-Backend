package com.medals.medalsbackend.config.security.websocket.verifier;

import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.entity.users.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.Map;

@RequiredArgsConstructor
public class RoleBasedWebsocketVerifier implements WebsocketEndpointVerifier {

  private final UserType userType;

  @Override
  public boolean verify(String url, StompHeaderAccessor headers) {
    Map<String, Object> attributes = headers.getSessionAttributes();
    if (attributes != null && attributes.containsKey("user")) {
      UserEntity user = (UserEntity) attributes.get("user");
      return user.getType().equals(userType);
    }
    return false;
  }
}
