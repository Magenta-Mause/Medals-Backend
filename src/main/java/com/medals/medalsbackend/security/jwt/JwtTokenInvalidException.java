package com.medals.medalsbackend.security.jwt;

import com.medals.medalsbackend.exceptions.GenericAPIRequestException;
import org.springframework.http.HttpStatus;

public class JwtTokenInvalidException extends GenericAPIRequestException {
    public JwtTokenInvalidException() {
        super("Provided JWT Token is invalid", HttpStatus.BAD_REQUEST);
    }
}
