package com.medals.medalsbackend.exception;

import org.springframework.http.HttpStatus;

public class AthleteAlreadyRequestedException extends GenericAPIRequestException {
    public AthleteAlreadyRequestedException() {
        super("Trainer already controlling requested Athlete", HttpStatus.NOT_FOUND);
    }
}
