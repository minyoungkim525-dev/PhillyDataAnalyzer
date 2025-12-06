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
 * JUnit test class for HousingProcessor.getAverageLivableArea method.
 * Tests all cases to achieve 100% statement coverage.
 * Uses manual mocks instead of Mockito.
 */
class GetAverageLivableAreaTest {

    private HousingReader housingReader;
    private PopulationReader populationReader;
    private HousingProcessor processor;

    @BeforeEach
    void setUp() {
        // Reset singleton instance for each test
        HousingProcessor.resetInstance();
    }

    @Test
    void testGetAverageLivableArea_ValidAreas() throws IOException {
        // Test case: Calculate average with valid livable areas
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

        int result = processor.getAverageLivableArea(zipCode);

        assertEquals(2000, result);
    }

    @Test
    void testGetAverageLivableArea_WithInvalidValues() throws IOException {
        // Test case: Ignore null, zero, and negative values
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, 200000, null),  // null livable area
                new House(zipCode, 300000, 0),     // zero livable area
                new House(zipCode, 400000, -500),   // negative livable area
                new House(zipCode, 500000, 3000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result = processor.getAverageLivableArea(zipCode);

        assertEquals(2000, result); // (1000 + 3000) / 2
    }

    @Test
    void testGetAverageLivableArea_NoValidValues() throws IOException {
        // Test case: All values are invalid
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, null),
                new House(zipCode, 200000, 0),
                new House(zipCode, 300000, -1000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result = processor.getAverageLivableArea(zipCode);

        assertEquals(0, result);
    }

    @Test
    void testGetAverageLivableArea_EmptyList() throws IOException {
        // Test case: No houses for ZIP code
        int zipCode = 19104;
        List<House> houses = new ArrayList<>();

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result = processor.getAverageLivableArea(zipCode);

        assertEquals(0, result);
    }

    @Test
    void testGetAverageLivableArea_Rounding() throws IOException {
        // Test case: Test rounding behavior
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, 200000, 1001)  // Average = 1000.5, should round to 1001
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result = processor.getAverageLivableArea(zipCode);

        assertEquals(1001, result);
    }

    @Test
    void testGetAverageLivableArea_Memoization() throws IOException {
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

        int result1 = processor.getAverageLivableArea(zipCode);
        int result2 = processor.getAverageLivableArea(zipCode);

        assertEquals(1500, result1);
        assertEquals(1500, result2);
    }

    @Test
    void testGetAverageLivableArea_DifferentZipCodes() throws IOException {
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

        int result19104 = processor.getAverageLivableArea(19104);
        int result19105 = processor.getAverageLivableArea(19105);

        assertEquals(1500, result19104);
        assertEquals(3500, result19105);
    }

    @Test
    void testGetAverageLivableArea_IOException() throws IOException {
        // Test case: Handle IOException gracefully
        int zipCode = 19104;

        housingReader = new TestHousingReader(true); // throws IOException
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result = processor.getAverageLivableArea(zipCode);

        assertEquals(0, result);
    }
}
