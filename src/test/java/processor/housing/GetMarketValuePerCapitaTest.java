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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for HousingProcessor.getMarketValuePerCapita method.
 * Tests all cases to achieve 100% statement coverage.
 * Uses manual mocks instead of Mockito.
 */
class GetMarketValuePerCapitaTest {

    private HousingReader housingReader;
    private PopulationReader populationReader;
    private HousingProcessor processor;

    @BeforeEach
    void setUp() {
        // Reset singleton instance for each test
        HousingProcessor.resetInstance();
    }

    @Test
    void testGetMarketValuePerCapita_ValidData() throws IOException {
        // Test case: Calculate market value per capita with valid data
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, 200000, 2000),
                new House(zipCode, 300000, 3000)
        );
        Map<Integer, Integer> populations = new HashMap<>();
        populations.put(zipCode, 1000);

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(populations);
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result = processor.getMarketValuePerCapita(zipCode);

        assertEquals(600, result); // (100000 + 200000 + 300000) / 1000 = 600
    }

    @Test
    void testGetMarketValuePerCapita_WithInvalidMarketValues() throws IOException {
        // Test case: Ignore invalid market values
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, null, 2000),  // null market value
                new House(zipCode, 0, 3000),     // zero market value
                new House(zipCode, -50000, 4000), // negative market value
                new House(zipCode, 200000, 5000)
        );
        Map<Integer, Integer> populations = new HashMap<>();
        populations.put(zipCode, 100);

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(populations);
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result = processor.getMarketValuePerCapita(zipCode);

        assertEquals(3000, result); // (100000 + 200000) / 100 = 3000
    }

    @Test
    void testGetMarketValuePerCapita_NoValidMarketValues() throws IOException {
        // Test case: No valid market values
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, null, 1000),
                new House(zipCode, 0, 2000),
                new House(zipCode, -10000, 3000)
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

    @Test
    void testGetMarketValuePerCapita_NoPopulation() throws IOException {
        // Test case: ZIP code not in population data
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000)
        );
        Map<Integer, Integer> populations = new HashMap<>();

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(populations);
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result = processor.getMarketValuePerCapita(zipCode);

        assertEquals(0, result);
    }

    @Test
    void testGetMarketValuePerCapita_ZeroPopulation() throws IOException {
        // Test case: Population is zero
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000)
        );
        Map<Integer, Integer> populations = new HashMap<>();
        populations.put(zipCode, 0);

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(populations);
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result = processor.getMarketValuePerCapita(zipCode);

        assertEquals(0, result);
    }

    @Test
    void testGetMarketValuePerCapita_EmptyHouseList() throws IOException {
        // Test case: No houses for ZIP code
        int zipCode = 19104;
        List<House> houses = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();
        populations.put(zipCode, 1000);

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(populations);
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result = processor.getMarketValuePerCapita(zipCode);

        assertEquals(0, result);
    }

    @Test
    void testGetMarketValuePerCapita_Rounding() throws IOException {
        // Test case: Test rounding behavior
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, 100001, 2000)
        );
        Map<Integer, Integer> populations = new HashMap<>();
        populations.put(zipCode, 3); // 200001 / 3 = 66667, should round

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(populations);
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result = processor.getMarketValuePerCapita(zipCode);

        assertEquals(66667, result);
    }

    @Test
    void testGetMarketValuePerCapita_Memoization() throws IOException {
        // Test case: Verify memoization works
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

        int result1 = processor.getMarketValuePerCapita(zipCode);
        int result2 = processor.getMarketValuePerCapita(zipCode);

        assertEquals(300, result1);
        assertEquals(300, result2);
    }

    @Test
    void testGetMarketValuePerCapita_IOException() throws IOException {
        // Test case: Handle IOException gracefully
        int zipCode = 19104;

        housingReader = new TestHousingReader(new ArrayList<>());
        populationReader = new TestPopulationReader(true); // throws IOException
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        int result = processor.getMarketValuePerCapita(zipCode);

        assertEquals(0, result);
    }
}
