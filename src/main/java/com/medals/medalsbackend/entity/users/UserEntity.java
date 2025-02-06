package com.medals.medalsbackend.entity.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@Entity(name = "user_entity")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@SuperBuilder
@NoArgsConstructor
public abstract class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "email", insertable = false, updatable = false)
  protected String email;

  @Column(name = "type")
  protected String type;

  @Column(name = "first_name", nullable = false)
  @JsonProperty("first_name")
  private String firstName;

  @Column(name = "last_name", nullable = false)
  @JsonProperty("last_name")
  private String lastName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "email", nullable = false, referencedColumnName = "email")
  @JsonIgnore
  private LoginEntry loginEntry;

}
