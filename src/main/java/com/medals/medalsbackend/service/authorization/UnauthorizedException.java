package com.medals.medalsbackend.service.authorization;

import com.medals.medalsbackend.exception.GenericAPIRequestException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends GenericAPIRequestException {
	public UnauthorizedException() {
		super("Missing authorization", HttpStatus.FORBIDDEN);
	}
}
