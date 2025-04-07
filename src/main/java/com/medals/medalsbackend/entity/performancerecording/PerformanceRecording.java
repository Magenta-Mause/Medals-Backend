package com.medals.medalsbackend.entity.performancerecording;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.medals.medalsbackend.entity.users.Athlete;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Entity(name = "performance_recording")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceRecording {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "athlete_id", insertable = false, updatable = false)
    @JsonProperty("athlete_id")
    private long athleteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "athlete_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private Athlete athlete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discipline_rating_id", nullable = false)
    @JsonProperty("discipline_rating_metric")
    private DisciplineRatingMetric disciplineRatingMetric;

    @Column(name = "rating_value", nullable = false)
    @JsonProperty("rating_value")
    private double ratingValue;

    @Column(name = "date_of_performance", nullable = false)
    @JsonProperty("date_of_performance")
    private Date dateOfPerformance;

    @Column(name = "age_at_recording", nullable = false)
    @JsonProperty("age_at_recording")
    private int ageAtRecording;
}
