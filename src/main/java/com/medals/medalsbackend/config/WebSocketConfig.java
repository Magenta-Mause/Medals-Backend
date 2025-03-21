package com.medals.medalsbackend.config;

import com.medals.medalsbackend.config.security.CorsConfigurationProperties;
import com.medals.medalsbackend.config.security.websocket.JwtChannelInterceptor;
import com.medals.medalsbackend.config.security.websocket.JwtHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@EnableConfigurationProperties(CorsConfigurationProperties.class)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final CorsConfigurationProperties corsConfigurationProperties;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
    private final JwtChannelInterceptor jwtChannelInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topics");
        config.setApplicationDestinationPrefixes("/api/v1/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/v1/ws")
          .setAllowedOrigins(corsConfigurationProperties.allowedOrigins())
          .addInterceptors(jwtHandshakeInterceptor)
          .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor);
    }
}
