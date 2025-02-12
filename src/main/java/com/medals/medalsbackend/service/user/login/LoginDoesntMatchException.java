package com.medals.medalsbackend.service.user.login;

import com.medals.medalsbackend.exception.GenericAPIRequestException;
import org.springframework.http.HttpStatus;

public class LoginDoesntMatchException extends GenericAPIRequestException {
    public LoginDoesntMatchException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
