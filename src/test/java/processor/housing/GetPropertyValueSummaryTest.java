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
 * Tests for getPropertyValueSummary(int zipCode) method.
 */
public class GetPropertyValueSummaryTest {

    @AfterEach
    public void tearDown() {
        HousingProcessor.resetInstance();
    }

    /**
     * Test case 1: Basic summary with odd number of values
     */
    @Test
    public void testBasicSummaryOddCount() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));
        houses.add(new House(19104, 200000, 2000));
        houses.add(new House(19104, 300000, 3000));
        houses.add(new House(19104, 400000, 4000));
        houses.add(new House(19104, 500000, 5000));

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        HousingProcessor.PropertyValueSummary summary = processor.getPropertyValueSummary(19104);

        assertEquals(100000, summary.getMin());
        assertEquals(500000, summary.getMax());
        assertEquals(300000, summary.getMedian());  // Middle value
    }

    /**
     * Test case 2: Summary with even number of values
     */
    @Test
    public void testBasicSummaryEvenCount() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));
        houses.add(new House(19104, 200000, 2000));
        houses.add(new House(19104, 300000, 3000));
        houses.add(new House(19104, 400000, 4000));

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        HousingProcessor.PropertyValueSummary summary = processor.getPropertyValueSummary(19104);

        assertEquals(100000, summary.getMin());
        assertEquals(400000, summary.getMax());
        assertEquals(250000, summary.getMedian());  // Average of 200000 and 300000
    }

    /**
     * Test case 3: Filters out null values
     */
    @Test
    public void testFiltersNullValues() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));
        houses.add(new House(19104, null, 2000));
        houses.add(new House(19104, 200000, 3000));
        houses.add(new House(19104, 300000, 4000));

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        HousingProcessor.PropertyValueSummary summary = processor.getPropertyValueSummary(19104);

        assertEquals(100000, summary.getMin());
        assertEquals(300000, summary.getMax());
        assertEquals(200000, summary.getMedian());
    }

    /**
     * Test case 4: Filters out zero values
     */
    @Test
    public void testFiltersZeroValues() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));
        houses.add(new House(19104, 0, 2000));
        houses.add(new House(19104, 200000, 3000));

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        HousingProcessor.PropertyValueSummary summary = processor.getPropertyValueSummary(19104);

        assertEquals(100000, summary.getMin());
        assertEquals(200000, summary.getMax());
        assertEquals(150000, summary.getMedian());
    }

    /**
     * Test case 5: Filters out negative values
     */
    @Test
    public void testFiltersNegativeValues() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));
        houses.add(new House(19104, -50000, 2000));
        houses.add(new House(19104, 200000, 3000));

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        HousingProcessor.PropertyValueSummary summary = processor.getPropertyValueSummary(19104);

        assertEquals(100000, summary.getMin());
        assertEquals(200000, summary.getMax());
    }

    /**
     * Test case 6: No valid houses returns all zeros
     */
    @Test
    public void testNoValidHouses() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, null, 1000));
        houses.add(new House(19104, 0, 2000));
        houses.add(new House(19104, -100, 3000));

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        HousingProcessor.PropertyValueSummary summary = processor.getPropertyValueSummary(19104);

        assertEquals(0, summary.getMin());
        assertEquals(0, summary.getMax());
        assertEquals(0, summary.getMedian());
    }

    /**
     * Test case 7: Single valid house
     */
    @Test
    public void testSingleHouse() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 150000, 1000));

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        HousingProcessor.PropertyValueSummary summary = processor.getPropertyValueSummary(19104);

        assertEquals(150000, summary.getMin());
        assertEquals(150000, summary.getMax());
        assertEquals(150000, summary.getMedian());
    }

    /**
     * Test case 8: Memoization - cached result
     */
    @Test
    public void testMemoization() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));
        houses.add(new House(19104, 200000, 2000));

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        HousingProcessor.PropertyValueSummary summary1 = processor.getPropertyValueSummary(19104);
        HousingProcessor.PropertyValueSummary summary2 = processor.getPropertyValueSummary(19104);

        // Should be same object from cache
        assertSame(summary1, summary2);
    }

    /**
     * Test case 9: toString method
     */
    @Test
    public void testToString() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));
        houses.add(new House(19104, 200000, 2000));
        houses.add(new House(19104, 300000, 3000));

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        HousingProcessor.PropertyValueSummary summary = processor.getPropertyValueSummary(19104);

        String result = summary.toString();

        assertTrue(result.contains("Min: 100000"));
        assertTrue(result.contains("Max: 300000"));
        assertTrue(result.contains("Median: 200000"));
    }

    private Map<Integer, Integer> createTestPopulations() {
        Map<Integer, Integer> populations = new HashMap<>();
        populations.put(19104, 50000);
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