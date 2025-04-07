package com.medals.medalsbackend.dto.authorization;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainerAccessRequestDto {
    private Long athleteId;
    private Long trainerId;
}
