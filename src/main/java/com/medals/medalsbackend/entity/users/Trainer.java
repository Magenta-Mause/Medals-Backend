package com.medals.medalsbackend.entity.users;

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
    @Column
    @ManyToMany(mappedBy = "trainers", cascade =  CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Athlete> managedAthletes;

    protected final UserType type = UserType.TRAINER;
}
