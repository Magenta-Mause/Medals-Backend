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
    @OneToMany(mappedBy = "trainer", cascade =  CascadeType.ALL, orphanRemoval = true)
    private List<Athlete> athleteList;

    protected final UserType type = UserType.TRAINER;
}
