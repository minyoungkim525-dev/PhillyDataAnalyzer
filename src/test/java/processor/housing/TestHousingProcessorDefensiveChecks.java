package processor.housing;

import common.House;
import data.HousingReader;
import data.PopulationReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processor.HousingProcessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for HousingProcessor defensive null checks.
 * Tests defensive programming checks that validate internal state.
 * Uses reflection to test scenarios that shouldn't occur in normal operation.
 */
class TestHousingProcessorDefensiveChecks {

    private HousingReader housingReader;
    private PopulationReader populationReader;
    private HousingProcessor processor;

    @BeforeEach
    void setUp() {
        HousingProcessor.resetInstance();
        housingReader = new TestHousingReader(new ArrayList<>());
        populationReader = new TestPopulationReader(new HashMap<>());
    }

    @Test
    void testConstructor_WithNullHousingReader() throws Exception {
        // Test case: Constructor should throw IllegalStateException when HousingReader is null
        // This tests line 51 - defensive check in constructor
        Constructor<HousingProcessor> constructor = HousingProcessor.class.getDeclaredConstructor(
                HousingReader.class, PopulationReader.class);
        constructor.setAccessible(true);

        assertThrows(IllegalStateException.class, () -> {
            constructor.newInstance(null, populationReader);
        });
    }

    @Test
    void testConstructor_WithNullPopulationReader() throws Exception {
        // Test case: Constructor should throw IllegalStateException when PopulationReader is null
        // This tests line 54 - defensive check in constructor
        Constructor<HousingProcessor> constructor = HousingProcessor.class.getDeclaredConstructor(
                HousingReader.class, PopulationReader.class);
        constructor.setAccessible(true);

        assertThrows(IllegalStateException.class, () -> {
            constructor.newInstance(housingReader, null);
        });
    }

    @Test
    void testGetAverageMarketValue_WithNullHousingReaderField() throws Exception {
        // Test case: Method should throw IllegalStateException if housingReader field is null
        // This tests line 89 - defensive check in getAverageMarketValue
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        
        // Use reflection to set housingReader field to null
        Field field = HousingProcessor.class.getDeclaredField("housingReader");
        field.setAccessible(true);
        field.set(processor, null);

        assertThrows(IllegalStateException.class, () -> {
            processor.getAverageMarketValue(19104);
        });
    }

    @Test
    void testGetAverageLivableArea_WithNullHousingReaderField() throws Exception {
        // Test case: Method should throw IllegalStateException if housingReader field is null
        // This tests line 120 - defensive check in getAverageLivableArea
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        
        Field field = HousingProcessor.class.getDeclaredField("housingReader");
        field.setAccessible(true);
        field.set(processor, null);

        assertThrows(IllegalStateException.class, () -> {
            processor.getAverageLivableArea(19104);
        });
    }

    @Test
    void testGetMarketValuePerCapita_WithNullHousingReaderField() throws Exception {
        // Test case: Method should throw IllegalStateException if housingReader field is null
        // This tests line 151 - defensive check in getMarketValuePerCapita
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        
        Field field = HousingProcessor.class.getDeclaredField("housingReader");
        field.setAccessible(true);
        field.set(processor, null);

        assertThrows(IllegalStateException.class, () -> {
            processor.getMarketValuePerCapita(19104);
        });
    }

    @Test
    void testGetMarketValuePerCapita_WithNullPopulationReaderField() throws Exception {
        // Test case: Method should throw IllegalStateException if populationReader field is null
        // This tests line 154 - defensive check in getMarketValuePerCapita
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        
        Field field = HousingProcessor.class.getDeclaredField("populationReader");
        field.setAccessible(true);
        field.set(processor, null);

        assertThrows(IllegalStateException.class, () -> {
            processor.getMarketValuePerCapita(19104);
        });
    }

    @Test
    void testGetPropertyValueSummary_WithNullHousingReaderField() throws Exception {
        // Test case: Method should throw IllegalStateException if housingReader field is null
        // This tests line 204 - defensive check in getPropertyValueSummary
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        
        Field field = HousingProcessor.class.getDeclaredField("housingReader");
        field.setAccessible(true);
        field.set(processor, null);

        assertThrows(IllegalStateException.class, () -> {
            processor.getPropertyValueSummary(19104);
        });
    }

    @Test
    void testGetHousesByZipCode_WithNullHousingReaderField() throws Exception {
        // Test case: Private method should throw IllegalStateException if housingReader field is null
        // This tests line 266 - defensive check in getHousesByZipCode
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        
        Field field = HousingProcessor.class.getDeclaredField("housingReader");
        field.setAccessible(true);
        field.set(processor, null);

        // Call a public method that uses getHousesByZipCode
        assertThrows(IllegalStateException.class, () -> {
            processor.getAverageMarketValue(19104);
        });
    }

    @Test
    void testGetHouseIterator_WithNullHousesList() throws Exception {
        // Test case: Method should throw IllegalStateException if houses list is null
        // This tests line 331 - defensive check in getHouseIterator
        // Since getHousesByZipCode always returns a list (never null in normal operation),
        // we use reflection to directly test the null check by creating a scenario where
        // the cache could theoretically contain null (though it shouldn't in practice)
        
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();
        
        // Use reflection to access the housesByZipCache and set it to null for a zip code
        // This simulates the defensive check scenario
        Field cacheField = HousingProcessor.class.getDeclaredField("housesByZipCache");
        cacheField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<Integer, List<House>> cache = (java.util.Map<Integer, List<House>>) cacheField.get(processor);
        cache.put(19104, null); // Put null in cache to trigger the defensive check
        
        // Now when getHouseIterator is called, getHousesByZipCode will return null from cache
        // and line 331 should throw IllegalStateException
        assertThrows(IllegalStateException.class, () -> {
            processor.getHouseIterator(19104);
        });
    }

    @Test
    void testCalculateAverageMarketValuesParallel_WithNullHousingReaderField() throws Exception {
        // Test case: Method should throw IllegalStateException if housingReader field is null
        // This tests line 345 - defensive check in calculateAverageMarketValuesParallel
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        
        Field field = HousingProcessor.class.getDeclaredField("housingReader");
        field.setAccessible(true);
        field.set(processor, null);

        assertThrows(IllegalStateException.class, () -> {
            processor.calculateAverageMarketValuesParallel(19104);
        });
    }
}

