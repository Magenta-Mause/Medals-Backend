package com.medals.medalsbackend.exceptions;

import org.springframework.http.HttpStatusCode;

public class AthleteNotFoundException extends GenericAPIRequestException {
    AthleteNotFoundException(String athleteId) {
        super("Athlete with id not found [id: " + athleteId + "]", HttpStatusCode.valueOf(404));
    }

    public static AthleteNotFoundException fromAthleteId(String athleteId) {
        return new AthleteNotFoundException(athleteId);
    }
}
