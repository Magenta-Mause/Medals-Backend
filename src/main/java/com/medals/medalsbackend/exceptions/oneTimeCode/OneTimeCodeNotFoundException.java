package com.medals.medalsbackend.exceptions.oneTimeCode;

import com.medals.medalsbackend.exceptions.GenericAPIRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class OneTimeCodeNotFoundException extends GenericAPIRequestException {
  public OneTimeCodeNotFoundException() {
    super("Invalid one time code", HttpStatus.NOT_FOUND);
  }
}
