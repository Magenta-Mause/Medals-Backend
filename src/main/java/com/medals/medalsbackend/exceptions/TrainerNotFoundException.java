package com.medals.medalsbackend.exceptions;

import org.springframework.http.HttpStatus;

public class TrainerNotFoundException extends GenericAPIRequestException {
  TrainerNotFoundException(Long trainerId) {
    super("Trainer with id not found [id: " + trainerId + "]", HttpStatus.NOT_FOUND);
  }

  public static TrainerNotFoundException fromTrainerId(Long athleteId) {
    return new TrainerNotFoundException(athleteId);
  }
}
