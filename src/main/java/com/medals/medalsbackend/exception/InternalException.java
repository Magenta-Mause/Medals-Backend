package com.medals.medalsbackend.exception;

import org.springframework.http.HttpStatus;

public class InternalException extends GenericAPIRequestException {
    public InternalException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
