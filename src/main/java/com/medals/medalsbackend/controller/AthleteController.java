package com.medals.medalsbackend.controller;

import com.medals.medalsbackend.dto.AthleteDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/athlete")
@CrossOrigin("*")
public class AthleteController {

    @GetMapping("/hello")
    public ResponseEntity<AthleteDTO> helloWorld() {
        try {
            return ResponseEntity.ok(new AthleteDTO(2, "test", "last name", "test@gmail.com", LocalDate.of(2025, 1, 21), 'd'));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString(), e);
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<List<AthleteDTO>> accept(@RequestBody List<AthleteDTO> athleteDTOList) {
        try {
            for (AthleteDTO athleteDTO : athleteDTOList) {
                new AthleteDTO(athleteDTO.getId(), athleteDTO.getFirstName(), athleteDTO.getLastName(), athleteDTO.getEmail(), athleteDTO.getBirthdate(), athleteDTO.getGender());
            }
            System.out.println("All Athlete data valid");
            return ResponseEntity.ok(athleteDTOList);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString(), e);
        }

    }
}
