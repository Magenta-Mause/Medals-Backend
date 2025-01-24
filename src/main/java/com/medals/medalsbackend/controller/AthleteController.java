package com.medals.medalsbackend.controller;

import com.medals.medalsbackend.dto.AthleteDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


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
    public ResponseEntity<List<AthleteDTO>> accept(@RequestBody @Valid List<AthleteDTO> athleteDTOList) {
        System.out.println("All athlete data valid");
        return ResponseEntity.ok(athleteDTOList);
    }

}
