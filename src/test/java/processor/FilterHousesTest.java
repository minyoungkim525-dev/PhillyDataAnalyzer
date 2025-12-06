package processor;

import common.House;
import data.HousingReader;
import data.PopulationReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for HousingProcessor.filterHouses method.
 * Tests varargs functionality with multiple predicates.
 * Uses manual mocks instead of Mockito.
 */
class FilterHousesTest {

    private HousingReader housingReader;
    private PopulationReader populationReader;
    private HousingProcessor processor;

    @BeforeEach
    void setUp() {
        // Reset singleton instance for each test
        HousingProcessor.resetInstance();
        housingReader = new TestHousingReader(new java.util.ArrayList<>());
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
    }

    @Test
    void testFilterHouses_SinglePredicate() {
        // Test case: Filter with single predicate
        List<House> houses = Arrays.asList(
                new House(19104, 100000, 1000),
                new House(19104, 200000, 2000),
                new House(19105, 300000, 3000)
        );

        Predicate<House> zipCodeFilter = house -> house.getZip_code() != null && house.getZip_code() == 19104;
        List<House> result = processor.filterHouses(houses, zipCodeFilter);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(h -> h.getZip_code() == 19104));
    }

    @Test
    void testFilterHouses_MultiplePredicates() {
        // Test case: Filter with multiple predicates (varargs)
        List<House> houses = Arrays.asList(
                new House(19104, 100000, 1000),
                new House(19104, 200000, 2000),
                new House(19104, 300000, 500),
                new House(19105, 200000, 2000)
        );

        Predicate<House> zipCodeFilter = house -> house.getZip_code() != null && house.getZip_code() == 19104;
        Predicate<House> marketValueFilter = house -> house.getMarket_value() != null && house.getMarket_value() >= 200000;
        Predicate<House> livableAreaFilter = house -> house.getTotal_livable_area() != null && house.getTotal_livable_area() >= 1000;

        List<House> result = processor.filterHouses(houses, zipCodeFilter, marketValueFilter, livableAreaFilter);

        assertEquals(1, result.size());
        assertEquals(200000, result.get(0).getMarket_value());
    }

    @Test
    void testFilterHouses_NoPredicates() {
        // Test case: No predicates (empty varargs)
        List<House> houses = Arrays.asList(
                new House(19104, 100000, 1000),
                new House(19105, 200000, 2000)
        );

        List<House> result = processor.filterHouses(houses);

        assertEquals(2, result.size());
    }

    @Test
    void testFilterHouses_EmptyList() {
        // Test case: Empty house list
        List<House> houses = Arrays.asList();

        Predicate<House> filter = house -> house.getZip_code() == 19104;
        List<House> result = processor.filterHouses(houses, filter);

        assertTrue(result.isEmpty());
    }
}
