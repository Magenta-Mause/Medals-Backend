package com.medals.medalsbackend.service.user.login;

import com.medals.medalsbackend.exceptions.GenericAPIRequestException;
import org.springframework.http.HttpStatus;

public class EmailDoesntExistException extends GenericAPIRequestException {
    public EmailDoesntExistException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
