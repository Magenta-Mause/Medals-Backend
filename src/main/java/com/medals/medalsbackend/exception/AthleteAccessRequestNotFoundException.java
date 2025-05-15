package com.medals.medalsbackend.exception;

import org.springframework.http.HttpStatus;

public class AthleteAccessRequestNotFoundException extends GenericAPIRequestException {
    public AthleteAccessRequestNotFoundException(String requestUUID) {
        super("Access request with id: " + requestUUID + " not found", HttpStatus.NOT_FOUND);
    }
}
