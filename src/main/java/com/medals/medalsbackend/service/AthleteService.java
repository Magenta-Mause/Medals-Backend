package com.medals.medalsbackend.service;

import com.medals.medalsbackend.dto.AthleteDTO;
import com.medals.medalsbackend.entity.Athlete;
import com.medals.medalsbackend.repository.AthleteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AthleteService {
    private final AthleteRepository athleteRepository;

    @Autowired
    public AthleteService(AthleteRepository athleteRepository) {
        this.athleteRepository = athleteRepository;
    }

    public boolean addAthlete(AthleteDTO athleteDTO) {
        Athlete athlete = new Athlete();
        athlete.setBirthdate(athleteDTO.getBirthdate());
        athlete.setGender(athleteDTO.getGender());
        athlete.setEmail(athleteDTO.getEmail());
        athlete.setLastName(athleteDTO.getLastName());
        athlete.setFirstName(athleteDTO.getFirstName());

        athleteRepository.save(athlete);
        return true;
    }
}
