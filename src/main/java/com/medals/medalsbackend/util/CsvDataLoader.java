package com.medals.medalsbackend.util;

import com.medals.medalsbackend.entity.performancerecording.Discipline;
import com.medals.medalsbackend.entity.performancerecording.DisciplineCategory;
import com.medals.medalsbackend.entity.performancerecording.DisciplineRatingMetric;
import com.medals.medalsbackend.entity.performancerecording.RatingMetric;
import com.medals.medalsbackend.exception.CsvLoadingException;
import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class CsvDataLoader {

    @Value("${app.files.disciplines-csv}")
    private String DISCIPLINES_CSV;
    @Value("${app.files.rating-metrics-csv}")
    private String RATING_METRICS_CSV;

    public List<Discipline> loadDisciplines() {
        List<Discipline> disciplines = readCsv(DISCIPLINES_CSV).stream()
                .map(this::toDiscipline)
                .collect(Collectors.toList());

        log.info("Loaded {} disciplines.", disciplines.size());
        return disciplines;
    }

    public List<DisciplineRatingMetric> loadDisciplineRatingMetrics(Map<Long, Discipline> disciplineMap) {
        List<DisciplineRatingMetric> metrics = readCsv(RATING_METRICS_CSV).stream()
                .map(row -> toDisciplineRatingMetric(row, disciplineMap))
                .collect(Collectors.toList());

        log.info("Loaded {} discipline rating metrics.", metrics.size());
        return metrics;
    }

    private List<Map<String, String>> readCsv(String classpathResource) {
        try (CSVReaderHeaderAware reader = new CSVReaderHeaderAware(
                new InputStreamReader(new ClassPathResource(classpathResource).getInputStream()))) {

            return Stream.generate(() -> {
                        try {
                            return reader.readMap();
                        } catch (IOException | CsvValidationException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .takeWhile(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error loading CSV resource: {}", classpathResource, e);
            throw new CsvLoadingException("Error loading CSV: " + classpathResource, e);
        }
    }

    private Discipline toDiscipline(Map<String, String> row) {
        String description = row.get("DESCRIPTION");
        if (description != null && description.isEmpty()) {
            description = null;
        }

        return Discipline.builder()
                .id(Long.parseLong(row.get("ID")))
                .name(row.get("NAME"))
                .description(description)
                .category(DisciplineCategory.valueOf(row.get("CATEGORY").toUpperCase()))
                .unit(Discipline.Unit.valueOf(row.get("UNIT").toUpperCase()))
                .isMoreBetter(Boolean.parseBoolean(row.get("IS_MORE_BETTER")))
                .build();
    }

    private DisciplineRatingMetric toDisciplineRatingMetric(Map<String, String> row, Map<Long, Discipline> disciplineMap) {
        Discipline discipline = getDisciplineSafely(disciplineMap, row.get("DISCIPLINE_ID"));
        return DisciplineRatingMetric.builder()
                .discipline(discipline)
                .validIn(parseInt(row.get("VALID_IN"), "VALID_IN"))
                .startAge(parseInt(row.get("START_AGE"), "START_AGE"))
                .endAge(parseInt(row.get("END_AGE"), "END_AGE"))
                .ratingFemale(parseRatingMetric(row, "FEMALE"))
                .ratingMale(parseRatingMetric(row, "MALE"))
                .build();
    }

    private RatingMetric parseRatingMetric(Map<String, String> row, String genderSuffix) {
        return RatingMetric.builder()
                .bronzeRating(parseTimeOrDouble(row.get("BRONZE_" + genderSuffix)))
                .silverRating(parseTimeOrDouble(row.get("SILVER_" + genderSuffix)))
                .goldRating(parseTimeOrDouble(row.get("GOLD_" + genderSuffix)))
                .build();
    }

    Double parseTimeOrDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.contains(":") ? parseMinutesSeconds(value) : Double.valueOf(value);
    }

    private Double parseMinutesSeconds(String value) {
        String[] parts = value.split(":");
        return Double.valueOf(Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]));
    }

    private Discipline getDisciplineSafely(Map<Long, Discipline> disciplineMap, String id) {
        return Optional.ofNullable(disciplineMap.get(Long.parseLong(id)))
                .orElseThrow(() -> new CsvLoadingException("Discipline not found: " + id, null));
    }

    Integer parseInt(String value, String fieldName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new CsvLoadingException("Invalid integer format for " + fieldName + ": " + value, e);
        }
    }
}
