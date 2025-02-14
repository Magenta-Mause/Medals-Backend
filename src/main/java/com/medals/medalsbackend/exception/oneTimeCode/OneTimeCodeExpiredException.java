package com.medals.medalsbackend.exception.oneTimeCode;

import com.medals.medalsbackend.exception.GenericAPIRequestException;
import org.springframework.http.HttpStatus;

public class OneTimeCodeExpiredException extends GenericAPIRequestException {
  public OneTimeCodeExpiredException() {
    super("One time code expired", HttpStatus.BAD_REQUEST);
  }
}
