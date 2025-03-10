package com.medals.medalsbackend.config.security.websocket.verifier;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Component
public class WebsocketVerifier {
  private final Map<String, WebsocketEndpointVerifier> websocketVerifier = new HashMap<String, WebsocketEndpointVerifier>();

  public WebsocketVerifier addVerifier(String channel, WebsocketEndpointVerifier verifier) {
    websocketVerifier.put("^" + channel.replace("{userId}", "([1-9][0-9]*)") + "$", verifier);
    return this;
  }

  public boolean verify(String channel, StompHeaderAccessor accessor) {
    log.info("verify: " + channel);
    for (final Map.Entry<String, WebsocketEndpointVerifier> entry : websocketVerifier.entrySet()) {
      Pattern pattern = Pattern.compile(entry.getKey());
      if (pattern.matcher(channel).matches()) {
        return entry.getValue().verify(channel, accessor);
      }
    }
    return false;
  }
}
