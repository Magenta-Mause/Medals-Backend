package com.medals.medalsbackend.entity.performancerecording;

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
    private int startAge;
    @Column(nullable = false)
    private int endAge;

    @Column(nullable = false)
    private double bronzeRating;
    @Column(nullable = false)
    private double silverRating;
    @Column(nullable = false)
    private double goldRating;
}
