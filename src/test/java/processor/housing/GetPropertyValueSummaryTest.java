package processor.housing;

import common.House;
import data.HousingReader;
import data.PopulationReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processor.HousingProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for HousingProcessor.getPropertyValueSummary method.
 * Tests all cases to achieve 100% statement coverage.
 * Uses manual mocks instead of Mockito.
 */
class GetPropertyValueSummaryTest {

    private HousingReader housingReader;
    private PopulationReader populationReader;
    private HousingProcessor processor;

    @BeforeEach
    void setUp() {
        // Reset singleton instance for each test
        HousingProcessor.resetInstance();
    }

    @Test
    void testGetPropertyValueSummary_ValidValues() throws IOException {
        // Test case: Calculate min, max, and median with valid values
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, 200000, 2000),
                new House(zipCode, 300000, 3000),
                new House(zipCode, 400000, 4000),
                new House(zipCode, 500000, 5000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        HousingProcessor.PropertyValueSummary result = processor.getPropertyValueSummary(zipCode);

        assertEquals(100000, result.getMin());
        assertEquals(500000, result.getMax());
        assertEquals(300000, result.getMedian());
    }

    @Test
    void testGetPropertyValueSummary_WithInvalidValues() throws IOException {
        // Test case: Ignore null, zero, and negative values
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, null, 2000),  // null market value
                new House(zipCode, 0, 3000),     // zero market value
                new House(zipCode, -50000, 4000), // negative market value
                new House(zipCode, 200000, 5000),
                new House(zipCode, 300000, 6000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        HousingProcessor.PropertyValueSummary result = processor.getPropertyValueSummary(zipCode);

        assertEquals(100000, result.getMin());
        assertEquals(300000, result.getMax());
        assertEquals(200000, result.getMedian()); // median of [100000, 200000, 300000]
    }

    @Test
    void testGetPropertyValueSummary_EvenNumberOfValues() throws IOException {
        // Test case: Median calculation with even number of values
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

        HousingProcessor.PropertyValueSummary result = processor.getPropertyValueSummary(zipCode);

        assertEquals(100000, result.getMin());
        assertEquals(400000, result.getMax());
        assertEquals(250000, result.getMedian()); // (200000 + 300000) / 2
    }

    @Test
    void testGetPropertyValueSummary_OddNumberOfValues() throws IOException {
        // Test case: Median calculation with odd number of values
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

        HousingProcessor.PropertyValueSummary result = processor.getPropertyValueSummary(zipCode);

        assertEquals(100000, result.getMin());
        assertEquals(300000, result.getMax());
        assertEquals(200000, result.getMedian());
    }

    @Test
    void testGetPropertyValueSummary_SingleValue() throws IOException {
        // Test case: Single valid value
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 150000, 1000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        HousingProcessor.PropertyValueSummary result = processor.getPropertyValueSummary(zipCode);

        assertEquals(150000, result.getMin());
        assertEquals(150000, result.getMax());
        assertEquals(150000, result.getMedian());
    }

    @Test
    void testGetPropertyValueSummary_NoValidValues() throws IOException {
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

        HousingProcessor.PropertyValueSummary result = processor.getPropertyValueSummary(zipCode);

        assertEquals(0, result.getMin());
        assertEquals(0, result.getMax());
        assertEquals(0, result.getMedian());
    }

    @Test
    void testGetPropertyValueSummary_EmptyList() throws IOException {
        // Test case: No houses for ZIP code
        int zipCode = 19104;
        List<House> houses = new ArrayList<>();

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        HousingProcessor.PropertyValueSummary result = processor.getPropertyValueSummary(zipCode);

        assertEquals(0, result.getMin());
        assertEquals(0, result.getMax());
        assertEquals(0, result.getMedian());
    }

    @Test
    void testGetPropertyValueSummary_Memoization() throws IOException {
        // Test case: Verify memoization works
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

        HousingProcessor.PropertyValueSummary result1 = processor.getPropertyValueSummary(zipCode);
        HousingProcessor.PropertyValueSummary result2 = processor.getPropertyValueSummary(zipCode);

        assertEquals(100000, result1.getMin());
        assertEquals(100000, result2.getMin());
        assertEquals(300000, result1.getMax());
        assertEquals(300000, result2.getMax());
    }

    @Test
    void testGetPropertyValueSummary_DifferentZipCodes() throws IOException {
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

        HousingProcessor.PropertyValueSummary result19104 = processor.getPropertyValueSummary(19104);
        HousingProcessor.PropertyValueSummary result19105 = processor.getPropertyValueSummary(19105);

        assertEquals(100000, result19104.getMin());
        assertEquals(200000, result19104.getMax());
        assertEquals(300000, result19105.getMin());
        assertEquals(400000, result19105.getMax());
    }

    @Test
    void testGetPropertyValueSummary_IOException() throws IOException {
        // Test case: Handle IOException gracefully
        int zipCode = 19104;

        housingReader = new TestHousingReader(true); // throws IOException
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        HousingProcessor.PropertyValueSummary result = processor.getPropertyValueSummary(zipCode);

        assertEquals(0, result.getMin());
        assertEquals(0, result.getMax());
        assertEquals(0, result.getMedian());
    }

    @Test
    void testGetPropertyValueSummary_UnsortedValues() throws IOException {
        // Test case: Values not in sorted order (should be sorted internally)
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 500000, 1000),
                new House(zipCode, 100000, 2000),
                new House(zipCode, 300000, 3000),
                new House(zipCode, 200000, 4000),
                new House(zipCode, 400000, 5000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        HousingProcessor.PropertyValueSummary result = processor.getPropertyValueSummary(zipCode);

        assertEquals(100000, result.getMin());
        assertEquals(500000, result.getMax());
        assertEquals(300000, result.getMedian());
    }
}
