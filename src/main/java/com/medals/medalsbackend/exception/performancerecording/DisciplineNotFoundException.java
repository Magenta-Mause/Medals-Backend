package com.medals.medalsbackend.exception.performancerecording;

import com.medals.medalsbackend.exception.GenericAPIRequestException;
import org.springframework.http.HttpStatus;

public class DisciplineNotFoundException extends GenericAPIRequestException {
    public DisciplineNotFoundException(long disciplineId) {
        super("Discipline with id: " + disciplineId + " not found", HttpStatus.NOT_FOUND);
    }
}
