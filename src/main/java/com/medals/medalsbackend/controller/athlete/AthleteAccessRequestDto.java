package com.medals.medalsbackend.controller.athlete;

import com.medals.medalsbackend.dto.AthleteDto;
import com.medals.medalsbackend.dto.TrainerDto;
import lombok.Builder;

@Builder
public record AthleteAccessRequestDto(String id, AthleteDto athlete, TrainerDto trainer) {
}
