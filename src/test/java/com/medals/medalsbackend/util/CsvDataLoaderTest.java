package com.medals.medalsbackend.util;

import com.medals.medalsbackend.entity.performancerecording.Discipline;
import com.medals.medalsbackend.exception.CsvLoadingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvDataLoaderTest {

    @InjectMocks
    private CsvDataLoader csvDataLoader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadDisciplines_Success() {
        List<Discipline> disciplines = csvDataLoader.loadDisciplines();

        assertNotNull(disciplines, "Disciplines list should not be null");
        assertFalse(disciplines.isEmpty(), "Disciplines list should not be empty");

        Discipline firstDiscipline = disciplines.get(0);
        assertNotNull(firstDiscipline.getId());
        assertNotNull(firstDiscipline.getName());
        assertNotNull(firstDiscipline.getCategory());
        assertNotNull(firstDiscipline.getUnit());
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

