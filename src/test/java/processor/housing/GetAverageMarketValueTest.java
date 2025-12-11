package processor.housing;

import common.House;
import data.HousingReader;
import data.PopulationReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processor.HousingProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for getAverageMarketValue(int zipCode) method.
 */
public class GetAverageMarketValueTest {

    @BeforeEach
    public void setUp() {
        HousingProcessor.resetInstance();
    }

    @AfterEach
    public void tearDown() {
        // Reset singleton after each test
        HousingProcessor.resetInstance();
    }

    /**
     * Test case 1: Basic average calculation
     */
    @Test
    public void testBasicAverage() throws IOException {
        // Create mock readers
        HousingReader housingReader = new MockHousingReader(createTestHouses());
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        // 19104 has houses with values: 100000, 200000, 300000
        // Average = 200000
        int result = processor.getAverageMarketValue(19104);

        assertEquals(200000, result);
    }

    /**
     * Test case 2: Filters out null values
     */
    @Test
    public void testFiltersNullValues() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));
        houses.add(new House(19104, null, 1000));  // Null value
        houses.add(new House(19104, 200000, 1000));

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        // Average of 100000 and 200000 = 150000
        int result = processor.getAverageMarketValue(19104);

        assertEquals(150000, result);
    }

    /**
     * Test case 3: Filters out zero values
     */
    @Test
    public void testFiltersZeroValues() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));
        houses.add(new House(19104, 0, 1000));      // Zero value
        houses.add(new House(19104, 200000, 1000));

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        int result = processor.getAverageMarketValue(19104);

        assertEquals(150000, result);
    }

    /**
     * Test case 4: Filters out negative values
     */
    @Test
    public void testFiltersNegativeValues() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));
        houses.add(new House(19104, -50000, 1000));  // Negative value
        houses.add(new House(19104, 200000, 1000));

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        int result = processor.getAverageMarketValue(19104);

        assertEquals(150000, result);
    }

    /**
     * Test case 5: Returns 0 for ZIP with no houses
     */
    @Test
    public void testNoHouses() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19103, 100000, 1000));  // Different ZIP

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        int result = processor.getAverageMarketValue(99999);

        assertEquals(0, result);
    }

    /**
     * Test case 6: Memoization - second call returns cached result
     */
    @Test
    public void testMemoization() throws IOException {
        HousingReader housingReader = new MockHousingReader(createTestHouses());
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        int result1 = processor.getAverageMarketValue(19104);
        int result2 = processor.getAverageMarketValue(19104);

        assertEquals(result1, result2);
    }

    // Helper methods
    private List<House> createTestHouses() {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));
        houses.add(new House(19104, 200000, 2000));
        houses.add(new House(19104, 300000, 3000));
        houses.add(new House(19103, 150000, 1500));
        return houses;
    }

    private Map<Integer, Integer> createTestPopulations() {
        Map<Integer, Integer> populations = new HashMap<>();
        populations.put(19104, 50000);
        populations.put(19103, 30000);
        return populations;
    }

    // Mock classes
    private static class MockHousingReader extends HousingReader {
        private final List<House> houses;

        public MockHousingReader(List<House> houses) {
            super("dummy.csv");
            this.houses = houses;
        }

        @Override
        public List<House> readData() {
            return houses;
        }
    }

    private static class MockPopulationReader implements PopulationReader {
        private final Map<Integer, Integer> populations;

        public MockPopulationReader(Map<Integer, Integer> populations) {
            this.populations = populations;
        }

        @Override
        public Map<Integer, Integer> readData() {
            return populations;
        }
    }
}