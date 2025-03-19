package com.medals.medalsbackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrunedAthleteDto {
    @NotNull
    @Size(min = 1, max = 255)
    @JsonProperty("first_name")
    private String firstName;

    @NotNull
    @Size(min = 1, max = 255)
    @JsonProperty("last_name")
    private String lastName;

    @NotNull
    private LocalDate birthdate;
}
