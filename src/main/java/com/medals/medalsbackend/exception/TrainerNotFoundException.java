package com.medals.medalsbackend.exception;

import org.springframework.http.HttpStatus;

public class TrainerNotFoundException extends GenericAPIRequestException {
  TrainerNotFoundException(Long trainerId) {
    super("Trainer with id not found [id: " + trainerId + "]", HttpStatus.NOT_FOUND);
  }

  public static TrainerNotFoundException fromTrainerId(Long athleteId) {
    return new TrainerNotFoundException(athleteId);
  }
}
