package com.medals.medalsbackend.entity.users;


import com.medals.medalsbackend.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@ToString
@DiscriminatorValue("ADMIN")
@NoArgsConstructor
@SuperBuilder
public class Admin extends UserEntity {
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    protected String type = "ADMIN";

}
