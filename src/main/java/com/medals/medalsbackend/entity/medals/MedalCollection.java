package com.medals.medalsbackend.entity.medals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.medals.medalsbackend.entity.users.Athlete;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class MedalCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id")
    private Athlete athlete;

    @Column
    @JsonProperty("endurance")
    private MedalType medalEndurance;

    @Column
    @JsonProperty("speed")
    private MedalType medalSpeed;

    @Column
    @JsonProperty("strength")
    private MedalType medalStrength;

    @Column
    @JsonProperty("coordination")
    private MedalType medalCoordination;
}
