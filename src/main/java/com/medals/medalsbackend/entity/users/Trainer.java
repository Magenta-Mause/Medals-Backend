package com.medals.medalsbackend.entity.users;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@DiscriminatorValue("TRAINER")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Trainer extends UserEntity {
    @JsonIgnore
    @ManyToMany(mappedBy = "trainersAssignedTo", cascade =  CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Athlete> assignedAthletes;

    protected final UserType type = UserType.TRAINER;
}
