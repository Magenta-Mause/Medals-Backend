package com.medals.medalsbackend.entity;

import com.medals.medalsbackend.dto.AthleteDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "athletes")
public class Athlete {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

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

    public AthleteDTO toDTO() {
        return AthleteDTO
                .builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .birthdate(birthdate)
                .gender(gender)
                .build();
    }
}
