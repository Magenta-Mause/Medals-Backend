package com.medals.medalsbackend.service;

import com.medals.medalsbackend.dto.AthleteDTO;
import com.medals.medalsbackend.entity.Athlete;
import com.medals.medalsbackend.repository.AthleteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AthleteService {
    private final AthleteRepository athleteRepository;
}
