package com.medals.medalsbackend.controller.trainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.dto.TrainerDto;
import com.medals.medalsbackend.dto.authorization.AthleteSearchDto;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.users.LoginEntry;
import com.medals.medalsbackend.exception.AthleteNotFoundException;
import com.medals.medalsbackend.exception.InternalException;
import com.medals.medalsbackend.exception.JwtTokenInvalidException;
import com.medals.medalsbackend.exception.TrainerNotFoundException;
import com.medals.medalsbackend.security.jwt.JwtTokenBody;
import com.medals.medalsbackend.service.user.TrainerService;
import com.medals.medalsbackend.service.user.login.EmailDoesntExistException;
import com.medals.medalsbackend.service.user.login.LoginEntryService;
import com.medals.medalsbackend.service.user.login.jwt.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.medals.medalsbackend.controller.BaseController.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH + "/trainers")
@RequiredArgsConstructor
public class TrainerController {
    private final TrainerService trainerService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<TrainerDto[]> getTrainers() {
        return ResponseEntity.ok(trainerService.getAllTrainers().stream().map(trainer -> objectMapper.convertValue(trainer, TrainerDto.class)).toArray(TrainerDto[]::new));
    }

    @PostMapping
    public ResponseEntity<TrainerDto> postTrainer(@Valid @RequestBody TrainerDto trainerDto) throws InternalException {
        return ResponseEntity.status(HttpStatus.CREATED).body(objectMapper.convertValue(trainerService.insertTrainer(trainerDto), TrainerDto.class));
    }

    @PostMapping(value = "/validate")
    public ResponseEntity<List<TrainerDto>> validateTrainers(@RequestBody @Valid List<TrainerDto> trainerDtoList) {
        return ResponseEntity.ok(trainerDtoList);
    }

    @DeleteMapping("/{trainerId}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long trainerId) throws TrainerNotFoundException {
        trainerService.deleteTrainer(trainerId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

    @GetMapping(value = "/{trainerId}")
    public ResponseEntity<TrainerDto> getTrainer(@PathVariable Long trainerId) throws TrainerNotFoundException {
        return ResponseEntity.ok(objectMapper.convertValue(trainerService.getTrainer(trainerId), TrainerDto.class));
    }

    @PostMapping(value = "/inviteAthlete")
    public ResponseEntity<Void> inviteAthlete(@RequestBody AthleteSearchDto athleteSearchDto) throws AthleteNotFoundException {
        trainerService.inviteAthlete(athleteSearchDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/searchAthletes")
    public ResponseEntity<List<Athlete>> searchAthletes(@RequestParam String athleteSearch) {
        return ResponseEntity.ok(trainerService.searchAthlete(athleteSearch));
    }
}
