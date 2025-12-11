package processor.housing;

import common.House;
import data.HousingReader;
import data.PopulationReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processor.HousingProcessor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for HousingProcessor edge cases and additional coverage.
 * Tests edge cases that might not be covered by other tests.
 */
class TestHousingProcessorEdgeCases {

    private HousingReader housingReader;
    private PopulationReader populationReader;
    private HousingProcessor processor;

    @BeforeEach
    void setUp() {
        HousingProcessor.resetInstance();
    }

    @Test
    void testGetHousesByZipCode_WithNullHouseInList() {
        // Test case: Test filtering with houses that have null zip_code
        List<House> houses = Arrays.asList(
                new House(19104, 100000, 1000),
                new House(null, 200000, 2000), // null zip_code
                new House(19104, 300000, 3000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // Should only return houses with matching zip_code (null ones filtered out)
        int result = processor.getAverageMarketValue(19104);
        assertEquals(200000, result); // (100000 + 300000) / 2
    }

    @Test
    void testGetHousesByZipCode_CacheWithDifferentZipCodes() {
        // Test case: Test that cache works correctly with different ZIP codes
        List<House> houses = Arrays.asList(
                new House(19104, 100000, 1000),
                new House(19105, 200000, 2000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result1 = processor.getAverageMarketValue(19104);
        int result2 = processor.getAverageMarketValue(19105);

        assertEquals(100000, result1);
        assertEquals(200000, result2);
    }

    @Test
    void testCalculateMedian_OddSize() {
        // Test case: Test calculateMedian with odd-sized list (indirectly through getPropertyValueSummary)
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

        HousingProcessor.PropertyValueSummary summary = processor.getPropertyValueSummary(zipCode);
        assertEquals(200000, summary.getMedian()); // Middle value for odd-sized list
    }

    @Test
    void testCalculateMedian_EvenSize() {
        // Test case: Test calculateMedian with even-sized list
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, 200000, 2000),
                new House(zipCode, 300000, 3000),
                new House(zipCode, 400000, 4000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        HousingProcessor.PropertyValueSummary summary = processor.getPropertyValueSummary(zipCode);
        assertEquals(250000, summary.getMedian()); // (200000 + 300000) / 2
    }

    @Test
    void testGetHouseIterator_Iteration() {
        // Test case: Test full iteration through iterator
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

        Iterator<House> iterator = processor.getHouseIterator(zipCode);
        int count = 0;
        while (iterator.hasNext()) {
            House house = iterator.next();
            assertNotNull(house);
            assertEquals(zipCode, house.getZip_code());
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    void testClearCache() {
        // Test case: Test that clearCache works correctly
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // Populate cache
        processor.getAverageMarketValue(zipCode);
        
        // Clear cache
        processor.clearCache();
        
        // Should recalculate (not use cache)
        int result = processor.getAverageMarketValue(zipCode);
        assertEquals(100000, result);
    }

    @Test
    void testGetMarketValuePerCapita_WithZeroTotalMarketValue() {
        // Test case: Test when total market value sum is zero (all invalid values)
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, null, 1000),
                new House(zipCode, 0, 2000),
                new House(zipCode, -1000, 3000)
        );
        Map<Integer, Integer> populations = new HashMap<>();
        populations.put(zipCode, 1000);

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(populations);
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result = processor.getMarketValuePerCapita(zipCode);
        assertEquals(0, result);
    }
}

