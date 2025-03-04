package com.medals.medalsbackend.exception.performancerecording;

import com.medals.medalsbackend.exception.GenericAPIRequestException;
import org.springframework.http.HttpStatus;

public class NoMatchingDisciplineRatingFoundForAge extends GenericAPIRequestException {
    public NoMatchingDisciplineRatingFoundForAge(Long disciplineId) {
        super("No matching discipline metric for age found with id " + disciplineId, HttpStatus.BAD_REQUEST);
    }
}
