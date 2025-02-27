package com.medals.medalsbackend.entity.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
@DiscriminatorValue("ADMIN")
@NoArgsConstructor
@SuperBuilder
public class Admin extends UserEntity {
  protected final UserType type = UserType.ADMIN;
}
