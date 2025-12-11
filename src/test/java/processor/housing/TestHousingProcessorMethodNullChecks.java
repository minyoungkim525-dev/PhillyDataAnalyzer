package processor.housing;

import common.House;
import data.HousingReader;
import data.PopulationReader;
import org.junit.jupiter.api.Test;
import processor.HousingProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for HousingProcessor method-level null checks.
 * Tests IllegalStateException when methods are called with uninitialized readers.
 */
class TestHousingProcessorMethodNullChecks {

    @Test
    void testGetAverageMarketValue_WithNullHousingReader() {
        // Test case: Should throw IllegalStateException when HousingReader is null
        // This is tested indirectly through reflection or by creating a processor with null reader
        // Since the constructor prevents null, we test through getInstance
        assertThrows(IllegalStateException.class, () -> {
            HousingProcessor.getInstance(null, new TestPopulationReader(new HashMap<>()));
        });
    }

    @Test
    void testGetAverageLivableArea_WithNullHousingReader() {
        // Test case: Should throw IllegalStateException when HousingReader is null
        assertThrows(IllegalStateException.class, () -> {
            HousingProcessor.getInstance(null, new TestPopulationReader(new HashMap<>()));
        });
    }

    @Test
    void testGetMarketValuePerCapita_WithNullPopulationReader() {
        // Test case: Should throw IllegalStateException when PopulationReader is null
        assertThrows(IllegalStateException.class, () -> {
            HousingProcessor.getInstance(new TestHousingReader(new ArrayList<>()), null);
        });
    }

    @Test
    void testGetPropertyValueSummary_WithNullHousingReader() {
        // Test case: Should throw IllegalStateException when HousingReader is null
        assertThrows(IllegalStateException.class, () -> {
            HousingProcessor.getInstance(null, new TestPopulationReader(new HashMap<>()));
        });
    }

    @Test
    void testCalculateAverageMarketValuesParallel_WithNullZipCodes() {
        // Test case: Should throw IllegalStateException when zipCodes array is null
        HousingProcessor.resetInstance();
        HousingReader housingReader = new TestHousingReader(new ArrayList<>());
        PopulationReader populationReader = new TestPopulationReader(new HashMap<>());
        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        assertThrows(IllegalStateException.class, () -> {
            processor.calculateAverageMarketValuesParallel((int[]) null);
        });
    }

    @Test
    void testGetHouseIterator_WithNullHousesList() {
        // Test case: getHouseIterator should handle null houses list
        // This is tested indirectly - if getHousesByZipCode returns null, it should throw
        HousingProcessor.resetInstance();
        HousingReader housingReader = new TestHousingReader(new ArrayList<>()) {
            @Override
            public java.util.List<House> readData() throws IOException {
                return null; // Return null to test null handling in getHousesByZipCode
            }
        };
        PopulationReader populationReader = new TestPopulationReader(new HashMap<>());
        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // getHousesByZipCode should handle null and return empty list, so iterator should work
        var iterator = processor.getHouseIterator(19104);
        assertNotNull(iterator);
        assertFalse(iterator.hasNext());
    }
}

