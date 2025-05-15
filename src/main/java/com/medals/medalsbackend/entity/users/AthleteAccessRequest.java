package com.medals.medalsbackend.entity.users;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AthleteAccessRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;
    @Column(nullable = false)
    public long athleteId;
    @Column(nullable = false)
    public long trainerId;
}
