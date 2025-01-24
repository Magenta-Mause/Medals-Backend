package com.medals.medalsbackend.controller;

import com.medals.medalsbackend.api.ApiResponse;
import com.medals.medalsbackend.api.ApiStatus;
import com.medals.medalsbackend.dto.AthleteDTO;
import jakarta.validation.Valid;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/athletes")
@CrossOrigin("*")
public class AthleteController {
    private static final List<AthleteDTO> DUMMY_ATHLETES = List.of(
            AthleteDTO.builder().id("55065afd-dc8d-487c-8943-c95ec6380777").firstName("John").lastName("Doe").birthdate(LocalDate.now()).email("john@doe.org").gender('m').build(),
            AthleteDTO.builder().id("1ec21d92-b355-46bc-bd75-79c4b5f861ab").firstName("Jane").lastName("Smith").birthdate(LocalDate.now()).email("jane.smith@example.org").gender('f').build(),
            AthleteDTO.builder().id("28cb57dd-5386-463e-9f0a-c87700281a8e").firstName("Emily").lastName("Johnson").birthdate(LocalDate.now()).email("emily.johnson@example.org").gender('d').build()
    );

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


    /*
        Dummy Endpoints
    */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAthletes() {
        Map<String, Object> athletesHashMap = new HashMap<>();
        athletesHashMap.put("athletes", DUMMY_ATHLETES);

        Map<String, Object> body = new HashMap<>();
        body.put("status", "success");
        body.put("message", "Athletes list retrieved successfully");
        body.put("timestamp", LocalDateTime.now());
        body.put("data", athletesHashMap);

        return ResponseEntity.ok(body);
    }

    @GetMapping(value = "/{athleteId}/medals")
    public ResponseEntity<Map<String, Object>> getMedals(@PathVariable("athleteId") String athleteId) {
        Map<String, Object> medalsMap = new HashMap<>();
        medalsMap.put("endurance", "gold");
        medalsMap.put("speed", "silver");
        medalsMap.put("strength", "gold");
        medalsMap.put("coordination", "bronze");

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("athleteId", athleteId);
        dataMap.put("totalMedal", "gold");
        dataMap.put("bestMedals", medalsMap);

        Map<String, Object> body = new HashMap<>();
        body.put("status", "success");
        body.put("message", "Medals data retrieved successfully");
        body.put("timestamp", LocalDateTime.now());
        body.put("data", dataMap);

        return ResponseEntity.ok(body);
    }

    @GetMapping(value = "/{athleteId}/swimmingCertificate")
    public ResponseEntity<Map<String, Object>> getSwimmingCertificate(@PathVariable("athleteId") String athleteId) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("athleteId", athleteId);
        dataMap.put("swimmingCertificate", true);

        Map<String, Object> body = new HashMap<>();
        body.put("status", "success");
        body.put("message", "Swimming certificate data retrieved successfully");
        body.put("timestamp", LocalDateTime.now());
        body.put("data", dataMap);

        return ResponseEntity.ok(body);
    }
}
