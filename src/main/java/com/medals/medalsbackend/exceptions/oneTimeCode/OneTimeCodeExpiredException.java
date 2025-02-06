package com.medals.medalsbackend.exceptions.oneTimeCode;

import com.medals.medalsbackend.exceptions.GenericAPIRequestException;
import org.springframework.http.HttpStatus;

public class OneTimeCodeExpiredException extends GenericAPIRequestException {
  public OneTimeCodeExpiredException() {
    super("One time code expired", HttpStatus.BAD_REQUEST);
  }
}
