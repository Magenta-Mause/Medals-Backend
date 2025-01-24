package com.medals.medalsbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.medals.medalsbackend.entity.medal.MedalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AthleteDto {
    @NotNull
    public char gender;

    @UUID
    private String id;

    @NotNull
    @Size(min = 1, max = 255)
    @JsonProperty("first_name")
    private String firstName;

    @NotNull
    @Size(min = 1, max = 255)
    @JsonProperty("last_name")
    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotNull
    private LocalDate birthdate;

    @JsonProperty("total_medal")
    private MedalType totalMedal;

    @JsonProperty("swimming_certificate")
    private boolean swimmingCertificate = false;
}
