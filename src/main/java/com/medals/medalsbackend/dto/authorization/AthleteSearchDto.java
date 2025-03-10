package com.medals.medalsbackend.dto.authorization;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AthleteSearchDto {
    private Long athleteId;
    private Long trainerId;

}
