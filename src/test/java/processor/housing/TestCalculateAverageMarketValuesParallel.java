package processor.housing;

import common.House;
import data.HousingReader;
import data.PopulationReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processor.HousingProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for HousingProcessor.calculateAverageMarketValuesParallel method.
 * Tests parallel processing functionality and null checks.
 */
class TestCalculateAverageMarketValuesParallel {

    private HousingReader housingReader;
    private PopulationReader populationReader;
    private HousingProcessor processor;

    @BeforeEach
    void setUp() {
        HousingProcessor.resetInstance();
    }

    @Test
    void testCalculateAverageMarketValuesParallel_WithValidData() {
        // Test case: Parallel calculation with valid data
        List<House> houses = Arrays.asList(
                new House(19104, 100000, 1000),
                new House(19104, 200000, 2000),
                new House(19105, 300000, 3000),
                new House(19105, 400000, 4000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        Map<Integer, Integer> results = processor.calculateAverageMarketValuesParallel(19104, 19105);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(150000, results.get(19104)); // (100000 + 200000) / 2
        assertEquals(350000, results.get(19105)); // (300000 + 400000) / 2
    }

    @Test
    void testCalculateAverageMarketValuesParallel_WithSingleZipCode() {
        // Test case: Parallel calculation with single ZIP code
        List<House> houses = Arrays.asList(
                new House(19104, 100000, 1000),
                new House(19104, 200000, 2000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        Map<Integer, Integer> results = processor.calculateAverageMarketValuesParallel(19104);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(150000, results.get(19104));
    }

    @Test
    void testCalculateAverageMarketValuesParallel_WithEmptyZipCodes() {
        // Test case: Parallel calculation with no ZIP codes (empty varargs)
        List<House> houses = new ArrayList<>();

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        Map<Integer, Integer> results = processor.calculateAverageMarketValuesParallel();

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testCalculateAverageMarketValuesParallel_WithNoHouses() {
        // Test case: Parallel calculation when no houses exist
        List<House> houses = new ArrayList<>();

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        Map<Integer, Integer> results = processor.calculateAverageMarketValuesParallel(19104, 19105);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(0, results.get(19104));
        assertEquals(0, results.get(19105));
    }
}

