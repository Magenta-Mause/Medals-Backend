package com.medals.medalsbackend.service.performancerecording;

import com.medals.medalsbackend.exception.GenericAPIRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class PerformanceRecordingNotFoundException extends GenericAPIRequestException {

    public PerformanceRecordingNotFoundException(Long disciplineId) {
        super("Discipline with id " + disciplineId + " not found", HttpStatus.NOT_FOUND);
    }
}
