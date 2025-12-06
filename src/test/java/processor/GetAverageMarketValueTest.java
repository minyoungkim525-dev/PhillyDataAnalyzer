package processor;

import common.House;
import data.HousingReader;
import data.PopulationReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for HousingProcessor.getAverageMarketValue method.
 * Tests all cases to achieve 100% statement coverage.
 * Uses manual mocks instead of Mockito.
 */
class GetAverageMarketValueTest {

    private HousingReader housingReader;
    private PopulationReader populationReader;
    private HousingProcessor processor;

    @BeforeEach
    void setUp() {
        // Reset singleton instance for each test
        HousingProcessor.resetInstance();
    }

    @Test
    void testGetAverageMarketValue_ValidHouses() throws IOException {
        // Test case: Calculate average with valid market values
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

        int result = processor.getAverageMarketValue(zipCode);

        assertEquals(200000, result);
    }

    @Test
    void testGetAverageMarketValue_WithInvalidValues() throws IOException {
        // Test case: Ignore null, zero, and negative values
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, null, 2000),  // null market value
                new House(zipCode, 0, 3000),     // zero market value
                new House(zipCode, -50000, 4000), // negative market value
                new House(zipCode, 200000, 5000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result = processor.getAverageMarketValue(zipCode);

        assertEquals(150000, result); // (100000 + 200000) / 2
    }

    @Test
    void testGetAverageMarketValue_NoValidValues() throws IOException {
        // Test case: All values are invalid
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, null, 1000),
                new House(zipCode, 0, 2000),
                new House(zipCode, -10000, 3000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result = processor.getAverageMarketValue(zipCode);

        assertEquals(0, result);
    }

    @Test
    void testGetAverageMarketValue_EmptyList() throws IOException {
        // Test case: No houses for ZIP code
        int zipCode = 19104;
        List<House> houses = new ArrayList<>();

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result = processor.getAverageMarketValue(zipCode);

        assertEquals(0, result);
    }

    @Test
    void testGetAverageMarketValue_Rounding() throws IOException {
        // Test case: Test rounding behavior
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, 100001, 2000)  // Average = 100000.5, should round to 100001
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result = processor.getAverageMarketValue(zipCode);

        assertEquals(100001, result);
    }

    @Test
    void testGetAverageMarketValue_Memoization() throws IOException {
        // Test case: Verify memoization works
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, 200000, 2000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result1 = processor.getAverageMarketValue(zipCode);
        int result2 = processor.getAverageMarketValue(zipCode);

        assertEquals(150000, result1);
        assertEquals(150000, result2);
    }

    @Test
    void testGetAverageMarketValue_DifferentZipCodes() throws IOException {
        // Test case: Different ZIP codes should return different results
        List<House> allHouses = Arrays.asList(
                new House(19104, 100000, 1000),
                new House(19104, 200000, 2000),
                new House(19105, 300000, 3000),
                new House(19105, 400000, 4000)
        );

        housingReader = new TestHousingReader(allHouses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result19104 = processor.getAverageMarketValue(19104);
        int result19105 = processor.getAverageMarketValue(19105);

        assertEquals(150000, result19104);
        assertEquals(350000, result19105);
    }

    @Test
    void testGetAverageMarketValue_IOException() throws IOException {
        // Test case: Handle IOException gracefully
        int zipCode = 19104;

        housingReader = new TestHousingReader(true); // throws IOException
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result = processor.getAverageMarketValue(zipCode);

        assertEquals(0, result);
    }
}
