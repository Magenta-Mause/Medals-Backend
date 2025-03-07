package com.medals.medalsbackend.controller.athlete;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.entity.medals.MedalCollection;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.entity.users.UserType;
import com.medals.medalsbackend.exception.AthleteNotFoundException;
import com.medals.medalsbackend.exception.InternalException;
import com.medals.medalsbackend.service.authorization.AuthorizationService;
import com.medals.medalsbackend.service.authorization.NoAuthenticationFoundException;
import com.medals.medalsbackend.service.user.AthleteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static com.medals.medalsbackend.controller.BaseController.BASE_PATH;


@Slf4j
@RestController
@RequestMapping(BASE_PATH + "/athletes")
@RequiredArgsConstructor
public class AthleteController {
	private final AthleteService athleteService;
	private final ObjectMapper objectMapper;
	private final AuthorizationService authorizationService;

	@GetMapping
	public ResponseEntity<AthleteDto[]> getAthletes() throws NoAuthenticationFoundException, AthleteNotFoundException {
		UserEntity selectedUser = authorizationService.getSelectedUser();
		return ResponseEntity.ok(switch (selectedUser.getType()) {
			case UserType.ADMIN ->
					Arrays.stream(athleteService.getAthletes()).map(athlete -> objectMapper.convertValue(athlete, AthleteDto.class)).toArray(AthleteDto[]::new);
			case UserType.ATHLETE ->
					List.of(objectMapper.convertValue(athleteService.getAthlete(selectedUser.getId()), AthleteDto.class)).toArray(AthleteDto[]::new);
			case UserType.TRAINER ->
					Arrays.stream(athleteService.getAthletes()).map(athlete -> objectMapper.convertValue(athlete, AthleteDto.class)).toArray(AthleteDto[]::new); // TODO: Athlete assignment logic here
		});
	}

	@PostMapping
	public ResponseEntity<AthleteDto> postAthlete(@Valid @RequestBody AthleteDto athleteDto) throws InternalException {
		return ResponseEntity.status(HttpStatus.CREATED).body(objectMapper.convertValue(athleteService.insertAthlete(athleteDto), AthleteDto.class));
	}

	@PostMapping(value = "/validate")
	public ResponseEntity<List<AthleteDto>> validateAthletes(@RequestBody @Valid List<AthleteDto> athleteDtoList) {
		return ResponseEntity.ok(athleteDtoList);
	}

	@DeleteMapping("/{athleteId}")
	public ResponseEntity<Void> deleteAthlete(@PathVariable Long athleteId) throws AthleteNotFoundException {
		athleteService.deleteAthlete(athleteId);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
	}

	@GetMapping(value = "/{athleteId}")
	public ResponseEntity<AthleteDto> getAthlete(@PathVariable Long athleteId) throws AthleteNotFoundException {
		return ResponseEntity.ok(objectMapper.convertValue(athleteService.getAthlete(athleteId), AthleteDto.class));
	}

	@GetMapping(value = "/{athleteId}/medals")
	public ResponseEntity<MedalCollection> getMedals(@PathVariable Long athleteId) throws AthleteNotFoundException {
		return ResponseEntity.ok(athleteService.getAthleteMedalCollection(athleteId));
	}

	@GetMapping(value = "/{athleteId}/swimmingCertificate")
	public ResponseEntity<Boolean> getSwimmingCertificate(@PathVariable Long athleteId) throws AthleteNotFoundException {
		return ResponseEntity.ok(athleteService.getAthlete(athleteId).isSwimmingCertificate());
	}
}
