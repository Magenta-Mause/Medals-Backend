package com.medals.medalsbackend.exceptions;

import org.springframework.http.HttpStatus;

public class JwtTokenInvalidException extends GenericAPIRequestException {
    public JwtTokenInvalidException() {
        super("Provided JWT token is invalid", HttpStatus.BAD_REQUEST);
    }
}
