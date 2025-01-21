package com.medals.medalsbackend.entity;

import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "athletes")
@Setter
public class Athlete {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDate birthdate;

    @Column(nullable = false)
    private Character gender;
}
