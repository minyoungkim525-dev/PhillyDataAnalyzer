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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for getHouseIterator() method - demonstrates Iterator pattern.
 */
public class GetHouseIteratorTest {

    @AfterEach
    public void tearDown() {
        HousingProcessor.resetInstance();
    }

    /**
     * Test case 1: Iterate through houses
     */
    @Test
    public void testIterator() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));
        houses.add(new House(19104, 200000, 2000));
        houses.add(new House(19104, 300000, 3000));

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        Iterator<House> iterator = processor.getHouseIterator(19104);

        int count = 0;
        while (iterator.hasNext()) {
            House house = iterator.next();
            assertNotNull(house);
            assertEquals(19104, house.getZip_code());
            count++;
        }

        assertEquals(3, count);
    }

    /**
     * Test case 2: Empty iterator for ZIP with no houses
     */
    @Test
    public void testEmptyIterator() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19103, 100000, 1000));

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        Iterator<House> iterator = processor.getHouseIterator(99999);

        assertFalse(iterator.hasNext());
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