package com.medals.medalsbackend.dto.performancerecording;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceRecordingDto {
    @JsonProperty("athlete_id")
    private long athleteId;
    @JsonProperty("rating_value")
    private double ratingValue;
    @JsonProperty("discipline_id")
    private long disciplineId;
    @JsonProperty("date_of_performance")
    private Date dateOfPerformance;
}
