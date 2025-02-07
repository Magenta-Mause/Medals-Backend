package com.medals.medalsbackend.exceptions;

import org.springframework.http.HttpStatus;

public class InternalException extends GenericAPIRequestException {
    public InternalException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
