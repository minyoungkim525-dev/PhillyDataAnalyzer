package processor.housing;

import common.House;
import data.HousingReader;
import data.PopulationReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import processor.HousingProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for calculateAverageMarketValuesParallel() method - demonstrates Threads and Varargs.
 */
public class CalculateAverageMarketValuesParallelTest {

    @AfterEach
    public void tearDown() {
        HousingProcessor.resetInstance();
    }

    /**
     * Test case 1: Calculate for multiple ZIP codes in parallel
     */
    @Test
    public void testParallelCalculation() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));
        houses.add(new House(19104, 200000, 2000));
        houses.add(new House(19103, 150000, 1500));
        houses.add(new House(19103, 250000, 2500));

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        // VARARGS: passing multiple ZIP codes
        Map<Integer, Integer> results = processor.calculateAverageMarketValuesParallel(19104, 19103);

        assertEquals(2, results.size());
        assertEquals(150000, results.get(19104));
        assertEquals(200000, results.get(19103));
    }

    /**
     * Test case 2: Single ZIP code
     */
    @Test
    public void testSingleZipCode() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));
        houses.add(new House(19104, 200000, 2000));

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        Map<Integer, Integer> results = processor.calculateAverageMarketValuesParallel(19104);

        assertEquals(1, results.size());
        assertEquals(150000, results.get(19104));
    }

    /**
     * Test case 3: No ZIP codes (empty varargs)
     */
    @Test
    public void testNoZipCodes() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        Map<Integer, Integer> results = processor.calculateAverageMarketValuesParallel();

        assertTrue(results.isEmpty());
    }

    /**
     * Test case 4: Many ZIP codes (tests thread pool)
     */
    @Test
    public void testManyZipCodes() throws IOException {
        List<House> houses = new ArrayList<>();
        for (int zip = 19100; zip < 19110; zip++) {
            houses.add(new House(zip, 100000, 1000));
            houses.add(new House(zip, 200000, 2000));
        }

        Map<Integer, Integer> populations = new HashMap<>();
        for (int zip = 19100; zip < 19110; zip++) {
            populations.put(zip, 50000);
        }

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(populations);

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        Map<Integer, Integer> results = processor.calculateAverageMarketValuesParallel(
                19100, 19101, 19102, 19103, 19104, 19105, 19106, 19107, 19108, 19109
        );

        assertEquals(10, results.size());
        for (Integer value : results.values()) {
            assertEquals(150000, value);
        }
    }

    private Map<Integer, Integer> createTestPopulations() {
        Map<Integer, Integer> populations = new HashMap<>();
        populations.put(19104, 50000);
        populations.put(19103, 30000);
        return populations;
    }

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