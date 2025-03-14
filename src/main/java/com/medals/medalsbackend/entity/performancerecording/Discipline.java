package com.medals.medalsbackend.entity.performancerecording;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "discipline")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Discipline {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private DisciplineCategory category;

    @Column
    private String description;

    @Column(nullable = false)
    private Unit unit;

    @Column(name = "more_better", nullable = false)
    @JsonProperty("more_better")
    private boolean moreBetter;

    public enum Unit {
        meters,
        seconds
    }

}
