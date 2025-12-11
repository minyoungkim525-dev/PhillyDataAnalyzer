package processor.housing;

import common.House;
import data.HousingReader;
import data.PopulationReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processor.HousingProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for HousingProcessor exception handling.
 * Tests exception paths in getHousesByZipCode and getMarketValuePerCapita.
 */
class TestHousingProcessorExceptionHandling {

    private HousingReader housingReader;
    private PopulationReader populationReader;
    private HousingProcessor processor;

    @BeforeEach
    void setUp() {
        HousingProcessor.resetInstance();
    }

    @Test
    void testGetHousesByZipCode_IOExceptionHandling() {
        // Test case: getHousesByZipCode should handle IOException gracefully
        int zipCode = 19104;

        housingReader = new TestHousingReader(true); // throws IOException
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // This should not throw an exception, but return empty list
        int result = processor.getAverageMarketValue(zipCode);
        assertEquals(0, result);
    }

    @Test
    void testGetMarketValuePerCapita_IOExceptionHandling() {
        // Test case: getMarketValuePerCapita should handle IOException gracefully
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(true); // throws IOException
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // This should not throw an exception, but return 0
        int result = processor.getMarketValuePerCapita(zipCode);
        assertEquals(0, result);
    }

    @Test
    void testGetHousesByZipCode_WithNullHousesList() {
        // Test case: getHousesByZipCode should handle null houses list from reader
        int zipCode = 19104;

        // Create a custom mock that returns null
        housingReader = new TestHousingReader(new ArrayList<>()) {
            @Override
            public java.util.List<House> readData() throws IOException {
                return null; // Return null to test null handling
            }
        };
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // Should handle null gracefully and return empty list
        int result = processor.getAverageMarketValue(zipCode);
        assertEquals(0, result);
    }
}

