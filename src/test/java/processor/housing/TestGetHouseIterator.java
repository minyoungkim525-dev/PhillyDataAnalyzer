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
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for HousingProcessor.getHouseIterator method.
 * Tests iterator functionality and null handling.
 */
class TestGetHouseIterator {

    private HousingReader housingReader;
    private PopulationReader populationReader;
    private HousingProcessor processor;

    @BeforeEach
    void setUp() {
        HousingProcessor.resetInstance();
    }

    @Test
    void testGetHouseIterator_WithHouses() {
        // Test case: Iterator should return houses for a ZIP code
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, 200000, 2000),
                new House(19105, 300000, 3000) // Different ZIP code
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        Iterator<House> iterator = processor.getHouseIterator(zipCode);

        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        assertEquals(19104, iterator.next().getZip_code());
        assertTrue(iterator.hasNext());
        assertEquals(19104, iterator.next().getZip_code());
        assertFalse(iterator.hasNext());
    }

    @Test
    void testGetHouseIterator_EmptyList() {
        // Test case: Iterator should work with empty list
        int zipCode = 19104;
        List<House> houses = new ArrayList<>();

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        Iterator<House> iterator = processor.getHouseIterator(zipCode);

        assertNotNull(iterator);
        assertFalse(iterator.hasNext());
    }

    @Test
    void testGetHouseIterator_NoMatchingZipCode() {
        // Test case: Iterator should be empty when no houses match ZIP code
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(19105, 100000, 1000),
                new House(19106, 200000, 2000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        Iterator<House> iterator = processor.getHouseIterator(zipCode);

        assertNotNull(iterator);
        assertFalse(iterator.hasNext());
    }
}

