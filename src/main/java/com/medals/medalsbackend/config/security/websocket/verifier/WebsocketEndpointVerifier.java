package com.medals.medalsbackend.config.security.websocket.verifier;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public interface WebsocketEndpointVerifier {
  boolean verify(String url, StompHeaderAccessor headers);
}
