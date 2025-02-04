package com.medals.medalsbackend.entity.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@DiscriminatorValue("TRAINER")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Trainer extends UserEntity {
    @Column(name = "first_name", nullable = false)
    @JsonProperty("first_name")
    private String firstName;
    @Column(name = "last_name", nullable = false)
    @JsonProperty("last_name")
    private String lastName;
    protected String type = "TRAINER";
}
