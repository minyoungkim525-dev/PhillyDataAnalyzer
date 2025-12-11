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
 * Tests for Singleton pattern implementation.
 */
public class SingletonTest {

    @AfterEach
    public void tearDown() {
        HousingProcessor.resetInstance();
    }

    /**
     * Test case 1: getInstance returns same instance
     */
    @Test
    public void testSingletonSameInstance() throws IOException {
        HousingReader housingReader = new MockHousingReader(new ArrayList<>());
        PopulationReader populationReader = new MockPopulationReader(new HashMap<>());

        HousingProcessor instance1 = HousingProcessor.getInstance(housingReader, populationReader);
        HousingProcessor instance2 = HousingProcessor.getInstance(housingReader, populationReader);

        assertSame(instance1, instance2);
    }

    /**
     * Test case 2: resetInstance clears singleton
     */
    @Test
    public void testResetInstance() throws IOException {
        HousingReader housingReader = new MockHousingReader(new ArrayList<>());
        PopulationReader populationReader = new MockPopulationReader(new HashMap<>());

        HousingProcessor instance1 = HousingProcessor.getInstance(housingReader, populationReader);

        HousingProcessor.resetInstance();

        HousingProcessor instance2 = HousingProcessor.getInstance(housingReader, populationReader);

        assertNotSame(instance1, instance2);
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