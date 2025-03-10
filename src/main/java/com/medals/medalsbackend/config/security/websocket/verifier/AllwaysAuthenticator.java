package com.medals.medalsbackend.config.security.websocket.verifier;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public class AllwaysAuthenticator implements WebsocketEndpointVerifier{
  @Override
  public boolean verify(String url, StompHeaderAccessor headers) {
    return true;
  }
}
