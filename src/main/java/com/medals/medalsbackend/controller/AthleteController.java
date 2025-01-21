package com.medals.medalsbackend.controller;

import com.medals.medalsbackend.dto.AthleteDTO;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;


@RestController
@RequestMapping("/api/athlete")
@CrossOrigin("*")
public class AthleteController {
    @GetMapping("/hello")
    public AthleteDTO helloWorld() {
        return new AthleteDTO(2, "test", "last name", "test@gmail.com", LocalDate.of(2025, 1, 21), 'd');
    }
}
