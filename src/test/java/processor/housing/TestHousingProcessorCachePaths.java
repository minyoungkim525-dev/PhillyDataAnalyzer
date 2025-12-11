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
 * JUnit test class for HousingProcessor cache hit paths.
 * Tests that cache hits are properly tested for all methods.
 */
class TestHousingProcessorCachePaths {

    private HousingReader housingReader;
    private PopulationReader populationReader;
    private HousingProcessor processor;

    @BeforeEach
    void setUp() {
        HousingProcessor.resetInstance();
    }

    @Test
    void testGetAverageMarketValue_CacheHit() {
        // Test case: Verify cache hit path is covered
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, 200000, 2000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // First call - cache miss
        int result1 = processor.getAverageMarketValue(zipCode);
        assertEquals(150000, result1);

        // Second call - cache hit
        int result2 = processor.getAverageMarketValue(zipCode);
        assertEquals(150000, result2);
        assertEquals(result1, result2);
    }

    @Test
    void testGetAverageLivableArea_CacheHit() {
        // Test case: Verify cache hit path is covered
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, 200000, 2000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // First call - cache miss
        int result1 = processor.getAverageLivableArea(zipCode);
        assertEquals(1500, result1);

        // Second call - cache hit
        int result2 = processor.getAverageLivableArea(zipCode);
        assertEquals(1500, result2);
        assertEquals(result1, result2);
    }

    @Test
    void testGetMarketValuePerCapita_CacheHit() {
        // Test case: Verify cache hit path is covered
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, 200000, 2000)
        );
        Map<Integer, Integer> populations = new HashMap<>();
        populations.put(zipCode, 1000);

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(populations);
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // First call - cache miss
        int result1 = processor.getMarketValuePerCapita(zipCode);
        assertEquals(300, result1);

        // Second call - cache hit
        int result2 = processor.getMarketValuePerCapita(zipCode);
        assertEquals(300, result2);
        assertEquals(result1, result2);
    }

    @Test
    void testGetPropertyValueSummary_CacheHit() {
        // Test case: Verify cache hit path is covered
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, 200000, 2000),
                new House(zipCode, 300000, 3000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // First call - cache miss
        HousingProcessor.PropertyValueSummary result1 = processor.getPropertyValueSummary(zipCode);
        assertEquals(100000, result1.getMin());
        assertEquals(300000, result1.getMax());

        // Second call - cache hit
        HousingProcessor.PropertyValueSummary result2 = processor.getPropertyValueSummary(zipCode);
        assertEquals(result1.getMin(), result2.getMin());
        assertEquals(result1.getMax(), result2.getMax());
        assertEquals(result1.getMedian(), result2.getMedian());
    }

    @Test
    void testGetHousesByZipCode_CacheHit() {
        // Test case: Verify housesByZipCache hit path is covered
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(19105, 200000, 2000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // First call - cache miss, should populate cache
        int result1 = processor.getAverageMarketValue(zipCode);
        assertEquals(100000, result1);

        // Second call - should use cached houses list
        int result2 = processor.getAverageMarketValue(zipCode);
        assertEquals(100000, result2);
    }
}

