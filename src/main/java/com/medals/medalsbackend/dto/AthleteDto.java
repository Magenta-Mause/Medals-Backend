package com.medals.medalsbackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.medals.medalsbackend.entity.medals.MedalType;
import com.medals.medalsbackend.entity.swimCertificate.SwimCertificateType;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.users.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AthleteDto {
    @NotNull
    public Athlete.Gender gender;

    private Long id;

    @NotNull
    @Size(min = 1, max = 255)
    @JsonProperty("first_name")
    private String firstName;

    @NotNull
    @Size(min = 1, max = 255)
    @JsonProperty("last_name")
    private String lastName;

    @NotNull
    @Email(message = "Email is not valid", regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
    private String email;

    @NotNull
    private LocalDate birthdate;

    @JsonProperty("total_medal")
    private MedalType totalMedal;

    @JsonProperty("swimming_certificate")
    private SwimCertificateType swimmingCertificate = null;

    @Builder.Default
    private UserType type = UserType.ATHLETE;
}
