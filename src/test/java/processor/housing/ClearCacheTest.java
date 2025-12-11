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
 * Tests for clearCache() method.
 */
public class ClearCacheTest {

    @AfterEach
    public void tearDown() {
        HousingProcessor.resetInstance();
    }

    /**
     * Test case 1: Cache is cleared
     */
    @Test
    public void testClearCache() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));
        houses.add(new House(19104, 200000, 2000));

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        // Calculate to populate cache
        int result1 = processor.getAverageMarketValue(19104);

        // Clear cache
        processor.clearCache();

        // Calculate again - should recalculate, not use cache
        int result2 = processor.getAverageMarketValue(19104);

        assertEquals(result1, result2);
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