package com.medals.medalsbackend.controller;

import com.medals.medalsbackend.api.ApiResponse;
import com.medals.medalsbackend.api.ApiStatus;
import com.medals.medalsbackend.dto.AthleteDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/athletes")
@CrossOrigin("*")
public class AthleteController {
    @PostMapping(value = "/test")
    public ApiResponse<List<AthleteDTO>> postAthletes() {
        // TODO
        return ApiResponse.<List<AthleteDTO>>error(HttpStatus.INTERNAL_SERVER_ERROR)
                .status(ApiStatus.NOT_IMPLEMENTED)
                .build();
    }

    @PostMapping(value = "/validate")
    public ResponseEntity<List<AthleteDTO>> validateAthletes(@RequestBody @Valid List<AthleteDTO> athleteDTOList) {
        System.out.println("All athlete data valid");
        return ResponseEntity.ok(athleteDTOList);
    }
}
