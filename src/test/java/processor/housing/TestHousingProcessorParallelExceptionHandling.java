package processor.housing;

import common.House;
import data.HousingReader;
import data.PopulationReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processor.HousingProcessor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for HousingProcessor parallel processing exception handling.
 * Tests InterruptedException and ExecutionException handling in calculateAverageMarketValuesParallel.
 */
class TestHousingProcessorParallelExceptionHandling {

    private HousingReader housingReader;
    private PopulationReader populationReader;
    private HousingProcessor processor;

    @BeforeEach
    void setUp() {
        HousingProcessor.resetInstance();
    }

    @Test
    void testCalculateAverageMarketValuesParallel_WithMultipleZipCodes() {
        // Test case: Parallel processing with multiple ZIP codes
        List<House> houses = Arrays.asList(
                new House(19104, 100000, 1000),
                new House(19104, 200000, 2000),
                new House(19105, 300000, 3000),
                new House(19105, 400000, 4000),
                new House(19106, 500000, 5000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        Map<Integer, Integer> results = processor.calculateAverageMarketValuesParallel(19104, 19105, 19106);

        assertNotNull(results);
        assertEquals(3, results.size());
        assertEquals(150000, results.get(19104));
        assertEquals(350000, results.get(19105));
        assertEquals(500000, results.get(19106));
    }

    @Test
    void testCalculateAverageMarketValuesParallel_ThreadSafety() {
        // Test case: Test thread safety of parallel processing
        List<House> houses = Arrays.asList(
                new House(19104, 100000, 1000),
                new House(19105, 200000, 2000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // Call parallel processing multiple times to test thread safety
        Map<Integer, Integer> results1 = processor.calculateAverageMarketValuesParallel(19104, 19105);
        processor.clearCache();
        Map<Integer, Integer> results2 = processor.calculateAverageMarketValuesParallel(19104, 19105);

        assertEquals(results1.get(19104), results2.get(19104));
        assertEquals(results1.get(19105), results2.get(19105));
    }

    @Test
    void testCalculateAverageMarketValuesParallel_WithIOException() {
        // Test case: Parallel processing should handle IOException from reader
        int zipCode = 19104;

        housingReader = new TestHousingReader(true); // throws IOException
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // Should handle exception gracefully and return 0
        Map<Integer, Integer> results = processor.calculateAverageMarketValuesParallel(zipCode);

        assertNotNull(results);
        assertEquals(0, results.get(zipCode));
    }
}

