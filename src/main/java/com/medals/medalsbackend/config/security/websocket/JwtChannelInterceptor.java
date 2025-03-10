package com.medals.medalsbackend.config.security.websocket;

import com.medals.medalsbackend.config.security.websocket.verifier.WebsocketVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

  private final WebsocketVerifier websocketVerifier;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
      String subscribedEndpoint = accessor.getDestination();
      log.info("Subscribing to {}", subscribedEndpoint);
      if (websocketVerifier.verify(subscribedEndpoint, accessor)) {
        return message;
      } else {
        return null;
      }
    }
    return message;
  }
}
