package com.medals.medalsbackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class AthleteNotFoundException extends GenericAPIRequestException {
    AthleteNotFoundException(Long athleteId) {
        super("Athlete with id not found [id: " + athleteId + "]", HttpStatus.NOT_FOUND);
    }

    public static AthleteNotFoundException fromAthleteId(Long athleteId) {
        return new AthleteNotFoundException(athleteId);
    }
}
