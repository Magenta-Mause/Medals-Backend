package com.medals.medalsbackend.config.security.websocket;

import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.security.jwt.JwtTokenBody;
import com.medals.medalsbackend.security.jwt.JwtUtils;
import com.medals.medalsbackend.service.user.UserEntityService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
  private final UserEntityService userEntityService;
  private final JwtUtils jwtUtils;

  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
    if (request instanceof org.springframework.http.server.ServletServerHttpRequest) {
      HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
      String token = servletRequest.getParameter("authToken");
      String selectedUser = servletRequest.getParameter("selectedUser");
      if (token == null || selectedUser == null) {
        return false;
      }
      long userId = Long.parseLong(selectedUser);
      Optional<UserEntity> userEntity = userEntityService.findById(userId);
      String jwtExtractedEmail = jwtUtils.getJwtTokenUser(token, JwtTokenBody.TokenType.IDENTITY_TOKEN);
      if (userEntity.isEmpty()) {
        return false;
      }
      if (!userEntity.get().getEmail().equalsIgnoreCase(jwtExtractedEmail)) {
        return false;
      }
      attributes.put("userId", userId);
      attributes.put("userEmail", userEntity.get().getEmail());
      attributes.put("user", userEntity.get());
      return true;
    }
    return false;
  }

  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {}
}
