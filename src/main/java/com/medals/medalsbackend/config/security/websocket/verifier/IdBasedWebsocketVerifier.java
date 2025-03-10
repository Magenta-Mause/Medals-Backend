package com.medals.medalsbackend.config.security.websocket.verifier;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdBasedWebsocketVerifier implements WebsocketEndpointVerifier{

  private Pattern path;
  public IdBasedWebsocketVerifier(final String path) {
    this.path = Pattern.compile("^" + path.replace("{userId}", "([1-9][0-9]*)") + "$");
  }

  @Override
  public boolean verify(String url, StompHeaderAccessor accessor) {
    Matcher matcher = path.matcher(url);
    if (matcher.matches()) {
      final Long userId = Long.valueOf(matcher.group(1));
      Map<String, Object> attributes = accessor.getSessionAttributes();
      if (attributes == null || !attributes.containsKey("userId")) {
        return false;
      }
      return attributes.get("userId").equals(userId);
    }
    return false;
  }
}
