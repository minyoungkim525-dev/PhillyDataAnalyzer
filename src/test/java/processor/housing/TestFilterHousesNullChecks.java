package processor.housing;

import common.House;
import data.HousingReader;
import data.PopulationReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processor.HousingProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for HousingProcessor.filterHouses null checks.
 * Tests IllegalStateException handling for null parameters.
 */
class TestFilterHousesNullChecks {

    private HousingReader housingReader;
    private PopulationReader populationReader;
    private HousingProcessor processor;

    @BeforeEach
    void setUp() {
        HousingProcessor.resetInstance();
        housingReader = new TestHousingReader(new ArrayList<>());
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
    }

    @Test
    void testFilterHouses_WithNullHousesList() {
        // Test case: Should throw IllegalStateException when houses list is null
        Predicate<House> filter = house -> house.getZip_code() == 19104;
        
        assertThrows(IllegalStateException.class, () -> {
            processor.filterHouses(null, filter);
        });
    }

    @Test
    void testFilterHouses_WithNullPredicates() {
        // Test case: Should throw IllegalStateException when predicates array is null
        List<House> houses = Arrays.asList(
                new House(19104, 100000, 1000)
        );
        
        assertThrows(IllegalStateException.class, () -> {
            processor.filterHouses(houses, (Predicate<House>[]) null);
        });
    }

    @Test
    void testFilterHouses_WithNullPredicateInArray() {
        // Test case: Should handle null predicate in array (filter will throw NPE, but we test the null check first)
        List<House> houses = Arrays.asList(
                new House(19104, 100000, 1000)
        );
        
        // This should work - null predicates in array are allowed by the method signature
        // but will cause issues during filtering. We test that the null check for the array itself works.
        @SuppressWarnings("unchecked")
        Predicate<House>[] predicates = new Predicate[1];
        predicates[0] = null;
        
        // The method will try to filter with null predicate, which may cause issues
        // But the null check for the array parameter itself is what we're testing
        assertThrows(NullPointerException.class, () -> {
            processor.filterHouses(houses, predicates);
        });
    }
}

