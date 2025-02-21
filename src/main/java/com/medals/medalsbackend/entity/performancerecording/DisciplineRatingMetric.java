package com.medals.medalsbackend.entity.performancerecording;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity(name = "discipline_rating_metric")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DisciplineRatingMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "discipline_id", nullable = false)
    private Discipline discipline;

    @Column(nullable = false)
    @JsonProperty("start_age")
    private int startAge;
    @Column(nullable = false)
    @JsonProperty("end_age")
    private int endAge;

    @Column(nullable = false)
    @JsonProperty("bronze_rating")
    private double bronzeRating;
    @Column(nullable = false)
    @JsonProperty("silver_rating")
    private double silverRating;
    @Column(nullable = false)
    @JsonProperty("gold_rating")
    private double goldRating;
}
