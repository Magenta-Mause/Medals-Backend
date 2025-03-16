package com.medals.medalsbackend.entity.performancerecording;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingMetric {
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
