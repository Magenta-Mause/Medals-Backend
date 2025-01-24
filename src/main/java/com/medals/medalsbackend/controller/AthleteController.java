package com.medals.medalsbackend.controller;

import com.medals.medalsbackend.dto.AthleteDTO;
import jakarta.validation.Valid;
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
    @PostMapping("/accept")
    public ResponseEntity<List<AthleteDTO>> accept(@RequestBody @Valid List<AthleteDTO> athleteDTOList) {
        System.out.println("All athlete data valid");
        return ResponseEntity.ok(athleteDTOList);
    }

}
