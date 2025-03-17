package com.medals.medalsbackend;

import com.medals.medalsbackend.entity.performancerecording.*;
import com.opencsv.CSVReaderHeaderAware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.util.*;

@Slf4j
@Component
public class CsvDataLoader {

    public List<Discipline> loadDisciplines() {
        List<Discipline> disciplines = new ArrayList<>();
        try (CSVReaderHeaderAware reader = new CSVReaderHeaderAware(new InputStreamReader(
                new ClassPathResource("discipline_ratings/disciplines.csv").getInputStream()))) {

            Map<String, String> row;
            while ((row = reader.readMap()) != null) {
                Discipline discipline = Discipline.builder()
                        .id(Long.parseLong(row.get("ID")))
                        .name(row.get("NAME"))
                        .description(row.get("DESCRIPTION"))
                        .category(DisciplineCategory.valueOf(row.get("CATEGORY")))
                        .unit(Discipline.Unit.valueOf(row.get("UNIT")))
                        .isMoreBetter(Boolean.parseBoolean(row.get("IS_MORE_BETTER")))
                        .build();

                disciplines.add(discipline);
            }
        } catch (Exception e) {
            log.error("Error loading disciplines.csv", e);
            throw new RuntimeException(e);
        }
        return disciplines;
    }

    public List<DisciplineRatingMetric> loadDisciplineRatingMetrics(Map<Long, Discipline> disciplineMap) {
        List<DisciplineRatingMetric> metrics = new ArrayList<>();
        try (CSVReaderHeaderAware reader = new CSVReaderHeaderAware(new InputStreamReader(
                new ClassPathResource("discipline_ratings/discipline_rating_metric.csv").getInputStream()))) {

            Map<String, String> row;
            while ((row = reader.readMap()) != null) {
                Discipline discipline = disciplineMap.get(Long.parseLong(row.get("DISCIPLINE_ID")));

                DisciplineRatingMetric metric = DisciplineRatingMetric.builder()
                        .discipline(discipline)
                        .validIn(Integer.parseInt(row.get("VALID_IN")))
                        .startAge(Integer.parseInt(row.get("START_AGE")))
                        .endAge(Integer.parseInt(row.get("END_AGE")))
                        .ratingFemale(parseRatingMetric(row, "FEMALE"))
                        .ratingMale(parseRatingMetric(row, "MALE"))
                        .build();

                metrics.add(metric);
            }
        } catch (Exception e) {
            log.error("Error loading discipline_rating_metrics.csv", e);
            throw new RuntimeException(e);
        }
        return metrics;
    }

    private RatingMetric parseRatingMetric(Map<String, String> row, String genderSuffix) {
        return RatingMetric.builder()
                .bronzeRating(parseTimeOrDouble(row.get("BRONZE_" + genderSuffix)))
                .silverRating(parseTimeOrDouble(row.get("SILVER_" + genderSuffix)))
                .goldRating(parseTimeOrDouble(row.get("GOLD_" + genderSuffix)))
                .build();
    }

    private Double parseTimeOrDouble(String value) {
        // if the cell is empty or blank, return null
        if (value == null || value.isBlank()) {
            return null;
        }

        // if it's minutes:seconds, convert to total seconds
        if (value.contains(":")) {
            String[] parts = value.split(":");
            return Double.valueOf(Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]));
        }

        // otherwise parse as a normal number
        return Double.valueOf(value);
    }

}
