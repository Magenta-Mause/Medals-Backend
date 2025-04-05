package com.medals.medalsbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
public class AdminCreationDto {
    @NotNull
    @Size(min = 1, max = 255)
    @JsonProperty("first_name")
    private String firstName;
    @NotNull
    @Size(min = 1, max = 255)
    @JsonProperty("last_name")
    private String lastName;
    @NotNull
    @Size(min = 1, max = 255)
    private String email;
}
