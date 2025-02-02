package com.medals.medalsbackend.entity.users;

import com.medals.medalsbackend.entity.UserEntity;
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
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    protected String type = "TRAINER";
}
