package com.medals.medalsbackend.service.user.login;

import com.medals.medalsbackend.exception.GenericAPIRequestException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends GenericAPIRequestException {
    public EmailAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
