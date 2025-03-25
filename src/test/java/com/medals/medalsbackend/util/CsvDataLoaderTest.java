package com.medals.medalsbackend.util;

import com.medals.medalsbackend.entity.performancerecording.Discipline;
import com.medals.medalsbackend.entity.performancerecording.DisciplineRatingMetric;
import com.medals.medalsbackend.exception.CsvLoadingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CsvDataLoaderTest {

    @InjectMocks
    private CsvDataLoader csvDataLoader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Manually inject the CSV file paths for testing.
        ReflectionTestUtils.setField(csvDataLoader, "DISCIPLINES_CSV", "/test-disciplines.csv");
        ReflectionTestUtils.setField(csvDataLoader, "RATING_METRICS_CSV", "/test-rating-metrics.csv");
    }

    @Test
    void testLoadDisciplines_Success() {
        List<Discipline> disciplines = csvDataLoader.loadDisciplines();

        assertNotNull(disciplines, "Disciplines list should not be null");
        assertFalse(disciplines.isEmpty(), "Disciplines list should not be empty");

        Discipline firstDiscipline = disciplines.getFirst();
        assertNotNull(firstDiscipline.getId(), "First discipline's ID should not be null");
        assertNotNull(firstDiscipline.getName(), "First discipline's name should not be null");
        assertNotNull(firstDiscipline.getCategory(), "First discipline's category should not be null");
        assertNotNull(firstDiscipline.getUnit(), "First discipline's unit should not be null");
    }

    @Test
    void testLoadRatingMetric_Success() {
        List<Discipline> disciplines = csvDataLoader.loadDisciplines();
        assertNotNull(disciplines, "Disciplines list should not be null");
        assertFalse(disciplines.isEmpty(), "Disciplines list should not be empty");

        Map<Long, Discipline> disciplineMap = disciplines.stream()
                .collect(Collectors.toMap(Discipline::getId, d -> d));

        List<DisciplineRatingMetric> disciplineRatingMetrics = csvDataLoader.loadDisciplineRatingMetrics(disciplineMap);

        assertNotNull(disciplineRatingMetrics, "Rating Metric list should not be null");
        assertFalse(disciplineRatingMetrics.isEmpty(), "Rating Metric list should not be empty");

        DisciplineRatingMetric firstMetric = disciplineRatingMetrics.getFirst();
        assertNotNull(firstMetric.getDiscipline(), "Discipline should not be null");

        assertEquals(100L, firstMetric.getDiscipline().getId(), "Discipline ID should be '100'");
        assertEquals(2020, firstMetric.getValidIn(), "Valid in year should be '2020'");
        assertEquals(6, firstMetric.getStartAge(), "Start age should be 6");
        assertEquals(7, firstMetric.getEndAge(), "End age should be 7");
    }


    @Test
    void testParseInt_InvalidFormat() {
        CsvLoadingException ex = assertThrows(CsvLoadingException.class,
                () -> csvDataLoader.parseInt("invalidNumber", "TEST_FIELD"));

        assertTrue(ex.getMessage().contains("Invalid integer format for TEST_FIELD"));
    }

    @Test
    void testParseTimeOrDouble_MinutesSecondsFormat() {
        Double result = csvDataLoader.parseTimeOrDouble("1:30");
        assertEquals(90.0, result, "Should parse minutes and seconds correctly");
    }

    @Test
    void testParseTimeOrDouble_EmptyString() {
        assertNull(csvDataLoader.parseTimeOrDouble(""), "Empty string should return null");
        assertNull(csvDataLoader.parseTimeOrDouble("   "), "Blank string should return null");
    }
}
