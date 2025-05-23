package com.medals.medalsbackend.entity.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.medals.medalsbackend.entity.medals.MedalCollection;
import com.medals.medalsbackend.entity.medals.MedalType;
import com.medals.medalsbackend.entity.swimCertificate.SwimCertificateType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@DiscriminatorValue("ATHLETE")
@SuperBuilder
public class Athlete extends UserEntity {
    @Column(nullable = false)
    private LocalDate birthdate;

    @Column(nullable = false)
    private Gender gender;

    @JsonProperty("swimming_certificate")
    private SwimCertificateType swimmingCertificate = null;

    @Column
    @JsonProperty("total_medal")
    private MedalType totalMedal = null;

    @JsonIgnore
    @OneToOne(mappedBy = "athlete", cascade = CascadeType.ALL, orphanRemoval = true)
    private MedalCollection medalCollection;

    protected final UserType type = UserType.ATHLETE;

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

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "trainers_assigned_to_athlete",
            joinColumns = @JoinColumn(name = "athlete_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id")
    )
    private List<Trainer> trainersAssignedTo;
}
