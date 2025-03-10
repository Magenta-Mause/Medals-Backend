package com.medals.medalsbackend.entity.performancerecording;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
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

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "bronzeRating", column = @Column(name = "bronze_rating_male")),
            @AttributeOverride(name = "silverRating", column = @Column(name = "silver_rating_male")),
            @AttributeOverride(name = "goldRating", column = @Column(name = "gold_rating_male"))
    })
    @JsonProperty("rating_male")
    private RatingMetric ratingMale;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "bronzeRating", column = @Column(name = "bronze_rating_female")),
            @AttributeOverride(name = "silverRating", column = @Column(name = "silver_rating_female")),
            @AttributeOverride(name = "goldRating", column = @Column(name = "gold_rating_female"))
    })
    @JsonProperty("rating_female")
    private RatingMetric ratingFemale;
    @JsonProperty("valid_in")
    @Column(name = "valid_in")
    private int validIn;
}
