package com.medals.medalsbackend.entity.medal;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medal_collections")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedalCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

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
