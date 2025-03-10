package com.medals.medalsbackend.config.security;

import com.medals.medalsbackend.config.security.websocket.verifier.*;
import com.medals.medalsbackend.entity.users.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(CorsConfigurationProperties.class)
public class SecurityConfiguration {

	private final JwtFilter jwtFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(authorizeRequests -> authorizeRequests
						.requestMatchers("/api/v1/authorization/**").permitAll()
						.requestMatchers("/**").authenticated()
				)
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.cors(Customizer.withDefaults())
				.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource(CorsConfigurationProperties corsConfigurationProperties) {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(true);
		Arrays.stream(corsConfigurationProperties.allowedOrigins()).toList().forEach(configuration::addAllowedOrigin);
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public WebsocketVerifier websocketVerifier() {
		return new WebsocketVerifier()
			.addVerifier("/topics/athlete/creation/{userId}", new IdBasedWebsocketVerifier("/topics/athlete/creation/{userId}"))
			.addVerifier("/topics/athlete/update/{userId}", new IdBasedWebsocketVerifier("/topics/athlete/update/{userId}"))
			.addVerifier("/topics/athlete/deletion/{userId}", new IdBasedWebsocketVerifier("/topics/athlete/deletion/{userId}"))
			.addVerifier("/topics/discipline/creation", new AllwaysAuthenticator())
			.addVerifier("/topics/discipline/update", new AllwaysAuthenticator())
			.addVerifier("/topics/discipline/deletion", new AllwaysAuthenticator())
			.addVerifier("/topics/trainer/creation/admin", new RoleBasedWebsocketVerifier(UserType.ADMIN))
			.addVerifier("/topics/trainer/update/admin", new RoleBasedWebsocketVerifier(UserType.ADMIN))
			.addVerifier("/topics/trainer/deletion/admin", new RoleBasedWebsocketVerifier(UserType.ADMIN))
			.addVerifier("/topics/trainer/creation/{userId}", new IdBasedWebsocketVerifier("/topics/trainer/creation/{userId}"))
			.addVerifier("/topics/trainer/update/{userId}", new IdBasedWebsocketVerifier("/topics/trainer/update/{userId}"))
			.addVerifier("/topics/trainer/deletion/{userId}", new IdBasedWebsocketVerifier("/topics/trainer/deletion/{userId}"))
			.addVerifier("/topics/performance-recording/creation/{userId}", new IdBasedWebsocketVerifier("/topics/performance-recording/creation/{userId}"))
			.addVerifier("/topics/performance-recording/update/{userId}", new IdBasedWebsocketVerifier("/topics/performance-recording/update/{userId}"))
			.addVerifier("/topics/performance-recording/deletion/{userId}", new IdBasedWebsocketVerifier("/topics/performance-recording/deletion/{userId}"));
	}
}
