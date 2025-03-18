package com.medals.medalsbackend.service.authorization;

import com.medals.medalsbackend.exception.GenericAPIRequestException;
import org.springframework.http.HttpStatus;

public class NoAuthenticationFoundException extends GenericAPIRequestException {
	public NoAuthenticationFoundException() {
		super("No authorization found", HttpStatus.UNAUTHORIZED);
	}
}
