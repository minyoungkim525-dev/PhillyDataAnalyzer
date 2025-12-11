package processor.housing;

import common.House;
import data.HousingReader;
import data.PopulationReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processor.HousingProcessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
        Constructor<HousingProcessor> constructor = HousingProcessor.class.getDeclaredConstructor(
                HousingReader.class, PopulationReader.class);
        constructor.setAccessible(true);

        // Reflection wraps exceptions in InvocationTargetException
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
            constructor.newInstance(null, populationReader);
        });

        // Check the actual cause is IllegalStateException
        assertTrue(exception.getCause() instanceof IllegalStateException);
        assertEquals("HousingReader must not be null.", exception.getCause().getMessage());
    }

    @Test
    void testConstructor_WithNullPopulationReader() throws Exception {
        // Test case: Constructor should throw IllegalStateException when PopulationReader is null
        Constructor<HousingProcessor> constructor = HousingProcessor.class.getDeclaredConstructor(
                HousingReader.class, PopulationReader.class);
        constructor.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
            constructor.newInstance(housingReader, null);
        });

        assertTrue(exception.getCause() instanceof IllegalStateException);
        assertEquals("PopulationReader must not be null.", exception.getCause().getMessage());
    }

    @Test
    void testGetAverageMarketValue_WithNullHousingReaderField() throws Exception {
        // Test case: Method should throw IllegalStateException if housingReader field is null
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
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // Use reflection to access the housesByZipCache
        Field cacheField = HousingProcessor.class.getDeclaredField("housesByZipCache");
        cacheField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.concurrent.ConcurrentHashMap<Integer, List<House>> cache =
                (java.util.concurrent.ConcurrentHashMap<Integer, List<House>>) cacheField.get(processor);

        cache.put(19104, new ArrayList<>());

        java.util.Iterator<House> iterator = processor.getHouseIterator(19104);
        assertNotNull(iterator);
        assertFalse(iterator.hasNext());
    }

    @Test
    void testCalculateAverageMarketValuesParallel_WithNullHousingReaderField() throws Exception {
        // Test case: Method should throw IllegalStateException if housingReader field is null
        processor = HousingProcessor.getInstance(housingReader, populationReader);

        Field field = HousingProcessor.class.getDeclaredField("housingReader");
        field.setAccessible(true);
        field.set(processor, null);

        assertThrows(IllegalStateException.class, () -> {
            processor.calculateAverageMarketValuesParallel(19104);
        });
    }

    @Test
    void testFilterHouses_WithNullHousesList() {
        // Test case: Method should throw IllegalStateException if houses list is null
        processor = HousingProcessor.getInstance(housingReader, populationReader);

        assertThrows(IllegalStateException.class, () -> {
            processor.filterHouses(null, h -> h.getMarket_value() != null);
        });
    }

    @Test
    void testFilterHouses_WithNullPredicates() {
        // Test case: Method should throw IllegalStateException if predicates is null
        processor = HousingProcessor.getInstance(housingReader, populationReader);

        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));

        assertThrows(IllegalStateException.class, () -> {
            processor.filterHouses(houses, (java.util.function.Predicate<House>[]) null);
        });
    }

    @Test
    void testCalculateAverageMarketValuesParallel_WithNullZipCodes() {
        // Test case: Method should throw IllegalStateException if zipCodes array is null
        processor = HousingProcessor.getInstance(housingReader, populationReader);

        assertThrows(IllegalStateException.class, () -> {
            processor.calculateAverageMarketValuesParallel((int[]) null);
        });
    }

    @Test
    void testGetInstance_WithNullHousingReader() {
        // Test getInstance also checks for null
        assertThrows(IllegalStateException.class, () -> {
            HousingProcessor.getInstance(null, populationReader);
        });
    }

    @Test
    void testGetInstance_WithNullPopulationReader() {
        // Test getInstance also checks for null
        assertThrows(IllegalStateException.class, () -> {
            HousingProcessor.getInstance(housingReader, null);
        });
    }
}