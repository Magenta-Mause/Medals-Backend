package com.medals.medalsbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.entity.medal.MedalCollection;
import com.medals.medalsbackend.exceptions.AthleteNotFoundException;
import com.medals.medalsbackend.service.AthleteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/v1/athletes")
@RequiredArgsConstructor
public class AthleteController {
    private final AthleteService athleteService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<AthleteDto[]> getAthletes() {
        return ResponseEntity.ok(Arrays.stream(athleteService.getAthletes()).map(athlete -> objectMapper.convertValue(athlete, AthleteDto.class)).toArray(AthleteDto[]::new));
    }

    @PostMapping
    public ResponseEntity<AthleteDto> postAthletes(@Valid @RequestBody AthleteDto athleteDto) {
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
