package com.medals.medalsbackend.service.notifications.mail.mailApi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.service.notifications.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailServiceImpl implements MailService {
	private final Environment environment;
	private final ObjectMapper objectMapper;
	private final WebClient webClient = WebClient.create();
	@Value("${app.mailClient.url}")
	private String mailClientUrl;
	@Value("${app.mailClient.auth-key}")
	private String mailClientAuthKey;

	@SneakyThrows
	@Override
	public void sendEmail(String receiver, String subject, String message) {
		if (Arrays.stream(environment.getActiveProfiles()).toList().contains("test")) {
			return;
		}
		Map<String, String> body = Map.of(
				"recipient", receiver,
				"subject", subject,
				"body", message,
				"enableHtml", "true"
		);

		webClient.post().uri(mailClientUrl + "/mail")
				.bodyValue(body)
				.header(
						"Authorization", mailClientAuthKey
				).retrieve()
				.toBodilessEntity()
				.block();
	}
}
