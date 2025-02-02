package com.medals.medalsbackend.entity.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.medals.medalsbackend.entity.UserEntity;
import com.medals.medalsbackend.entity.medals.MedalCollection;
import com.medals.medalsbackend.entity.medals.MedalType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@DiscriminatorValue("ATHLETE")
@SuperBuilder
public class Athlete extends UserEntity {

    @Column(name = "first_name", nullable = false)
    @JsonProperty("first_name")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @JsonProperty("last_name")
    private String lastName;

    @Column(nullable = false)
    private LocalDate birthdate;

    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    @JsonProperty("swimming_certificate")
    private boolean swimmingCertificate = false;

    @Column
    @JsonProperty("total_medal")
    private MedalType totalMedal = null;

    @JsonIgnore
    @OneToOne(mappedBy = "athlete", cascade = CascadeType.ALL, orphanRemoval = true)
    private MedalCollection medalCollection;

    protected String type = "ATHLETE";

    public enum Gender {
        MALE, FEMALE, DIVERSE;

        @Override
        public String toString() {
            return switch (this) {
                case MALE -> "m";
                case FEMALE -> "f";
                case DIVERSE -> "d";
            };
        }
    }

    public Athlete email(String email) {
        this.email = email;
        return this;
    }
}
