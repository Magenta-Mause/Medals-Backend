package com.medals.medalsbackend.entity.performancerecording;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.medals.medalsbackend.entity.users.Athlete;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    @ToString.Exclude
    @JsonIgnore
    private Athlete athlete;

    @Column(name = "discipline_rating_id", insertable = false, updatable = false)
    @JsonProperty("discipline_rating_id")
    private long disciplineRatingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discipline_rating_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private DisciplineRatingMetric disciplineRatingMetric;

    @Column(name = "rating_value", nullable = false)
    @JsonProperty("rating_value")
    private double ratingValue;

    @Column(name = "recorded_at", nullable = false)
    @JsonProperty("date_recorded")
    private LocalDateTime dateRecorded;

    @Column(name = "age_at_recording", nullable = false)
    @JsonProperty("age_at_recording")
    private int ageAtRecording;
}
