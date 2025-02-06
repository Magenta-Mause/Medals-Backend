package com.medals.medalsbackend.entity.users;


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
  protected UserType type = UserType.ADMIN;
}
