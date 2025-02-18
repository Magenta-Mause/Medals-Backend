package com.medals.medalsbackend.exception.onetimecode;

import com.medals.medalsbackend.exception.GenericAPIRequestException;
import org.springframework.http.HttpStatus;

public class OneTimeCodeNotFoundException extends GenericAPIRequestException {
  public OneTimeCodeNotFoundException() {
    super("Invalid one time code", HttpStatus.NOT_FOUND);
  }
}
