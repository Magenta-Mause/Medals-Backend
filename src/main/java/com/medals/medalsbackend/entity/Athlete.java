package com.medals.medalsbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.medals.medalsbackend.entity.medal.MedalCollection;
import com.medals.medalsbackend.entity.medal.MedalType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;

@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "athletes")
public class Athlete {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "first_name", nullable = false)
  @JsonProperty("first_name")
  private String firstName;

  @Column(name = "last_name", nullable = false)
  @JsonProperty("last_name")
  private String lastName;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(nullable = false)
  private LocalDate birthdate;

  @Column(nullable = false)
  private Character gender;

  @Column(nullable = false)
  @JsonProperty("swimming_certificate")
  private boolean swimmingCertificate = false;

  @Column
  @JsonProperty("total_medal")
  private MedalType totalMedal = null;

  @Getter
  @Setter
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @ToString.Exclude
  @JsonIgnore
  private MedalCollection medalCollection;

}
