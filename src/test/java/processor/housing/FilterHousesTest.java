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
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for filterHouses() method - demonstrates Varargs usage.
 */
public class FilterHousesTest {

    @AfterEach
    public void tearDown() {
        HousingProcessor.resetInstance();
    }

    /**
     * Test case 1: Filter with single predicate
     */
    @Test
    public void testSinglePredicate() throws IOException {
        List<House> houses = createTestHouses();

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        // Filter: market value > 150000
        Predicate<House> predicate = h -> h.getMarket_value() != null && h.getMarket_value() > 150000;

        List<House> filtered = processor.filterHouses(houses, predicate);

        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(h -> h.getMarket_value() > 150000));
    }

    /**
     * Test case 2: Filter with multiple predicates (Varargs)
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testMultiplePredicates() throws IOException {
        List<House> houses = createTestHouses();

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        // Multiple predicates using varargs
        Predicate<House> predicate1 = h -> h.getMarket_value() != null && h.getMarket_value() > 100000;
        Predicate<House> predicate2 = h -> h.getTotal_livable_area() != null && h.getTotal_livable_area() > 1500;

        List<House> filtered = processor.filterHouses(houses, predicate1, predicate2);

        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(h ->
                h.getMarket_value() > 100000 && h.getTotal_livable_area() > 1500));
    }

    /**
     * Test case 3: No predicates returns all houses
     */
    @Test
    public void testNoPredicates() throws IOException {
        List<House> houses = createTestHouses();

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        List<House> filtered = processor.filterHouses(houses);

        assertEquals(houses.size(), filtered.size());
    }

    /**
     * Test case 4: Filter by ZIP code
     */
    @Test
    public void testFilterByZipCode() throws IOException {
        List<House> houses = createTestHouses();

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        Predicate<House> predicate = h -> h.getZip_code() != null && h.getZip_code() == 19104;

        List<House> filtered = processor.filterHouses(houses, predicate);

        assertEquals(3, filtered.size());
        assertTrue(filtered.stream().allMatch(h -> h.getZip_code() == 19104));
    }

    /**
     * Test case 5: Empty list
     */
    @Test
    public void testEmptyList() throws IOException {
        List<House> houses = new ArrayList<>();

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        Predicate<House> predicate = h -> h.getMarket_value() != null;

        List<House> filtered = processor.filterHouses(houses, predicate);

        assertTrue(filtered.isEmpty());
    }

    /**
     * Test case 6: Three predicates (testing varargs with 3 arguments)
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testThreePredicates() throws IOException {
        List<House> houses = createTestHouses();

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(createTestPopulations());

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        Predicate<House> pred1 = h -> h.getZip_code() != null && h.getZip_code() == 19104;
        Predicate<House> pred2 = h -> h.getMarket_value() != null && h.getMarket_value() > 150000;
        Predicate<House> pred3 = h -> h.getTotal_livable_area() != null && h.getTotal_livable_area() > 2000;

        List<House> filtered = processor.filterHouses(houses, pred1, pred2, pred3);

        assertEquals(1, filtered.size());
        assertEquals(19104, filtered.get(0).getZip_code());
        assertTrue(filtered.get(0).getMarket_value() > 150000);
        assertTrue(filtered.get(0).getTotal_livable_area() > 2000);
    }

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