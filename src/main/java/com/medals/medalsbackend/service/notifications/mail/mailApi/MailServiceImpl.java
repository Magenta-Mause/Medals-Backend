package com.medals.medalsbackend.service.notifications.mail.mailApi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.service.notifications.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

		webClient.post()
				.uri(mailClientUrl + "/mail")
				.bodyValue(body)
				.header("Authorization", mailClientAuthKey)
				.retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, resp ->
						resp.bodyToMono(String.class)
								.defaultIfEmpty("")
								.flatMap(errBody -> Mono.error(new RuntimeException(
										"Mail send failed with 4xx: " + resp.statusCode() + " body: " + errBody))))
				.onStatus(HttpStatusCode::is5xxServerError, resp ->
						resp.bodyToMono(String.class)
								.defaultIfEmpty("")
								.flatMap(errBody -> Mono.error(new RuntimeException(
										"Mail send failed with 5xx: " + resp.statusCode() + " body: " + errBody))))
				.toBodilessEntity()
				.doOnSuccess(entity -> log.info("Mail request sent to {} (status: {})", receiver,
						entity != null ? entity.getStatusCode() : "unknown"))
				.doOnError(ex -> log.error("Failed to send mail to {} with subject '{}': {}",
						receiver, subject, ex.toString(), ex))
				.subscribe();
	}
}
