package com.medals.medalsbackend.service.authorization;

import com.medals.medalsbackend.exception.GenericAPIRequestException;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends GenericAPIRequestException {
	public ForbiddenException() {
		super("Missing authorization", HttpStatus.FORBIDDEN);
	}
}
