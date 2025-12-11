package processor.housing;

import common.House;
import data.HousingReader;
import data.PopulationReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processor.HousingProcessor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for HousingProcessor private strategy methods.
 * Uses reflection to test the private strategy methods that are stored in the strategies map.
 */
class TestHousingProcessorStrategyMethods {

    private HousingReader housingReader;
    private PopulationReader populationReader;
    private HousingProcessor processor;

    @BeforeEach
    void setUp() {
        HousingProcessor.resetInstance();
    }

    @Test
    void testCalculateAverageMarketValue_StrategyMethod() throws Exception {
        // Test case: Test private calculateAverageMarketValue strategy method using reflection
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, 200000, 2000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // Use reflection to access private method
        Method method = HousingProcessor.class.getDeclaredMethod(
                "calculateAverageMarketValue", 
                Integer.class, 
                List.class, 
                Map.class
        );
        method.setAccessible(true);

        Integer result = (Integer) method.invoke(processor, zipCode, houses, new HashMap<>());
        assertEquals(150000, result);
    }

    @Test
    void testCalculateAverageLivableArea_StrategyMethod() throws Exception {
        // Test case: Test private calculateAverageLivableArea strategy method using reflection
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, 200000, 2000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // Use reflection to access private method
        Method method = HousingProcessor.class.getDeclaredMethod(
                "calculateAverageLivableArea", 
                Integer.class, 
                List.class, 
                Map.class
        );
        method.setAccessible(true);

        Integer result = (Integer) method.invoke(processor, zipCode, houses, new HashMap<>());
        assertEquals(1500, result);
    }

    @Test
    void testCalculateMarketValuePerCapita_StrategyMethod() throws Exception {
        // Test case: Test private calculateMarketValuePerCapita strategy method using reflection
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, 200000, 2000)
        );
        Map<Integer, Integer> populations = new HashMap<>();
        populations.put(zipCode, 1000);

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(populations);
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // Use reflection to access private method
        Method method = HousingProcessor.class.getDeclaredMethod(
                "calculateMarketValuePerCapita", 
                Integer.class, 
                List.class, 
                Map.class
        );
        method.setAccessible(true);

        Integer result = (Integer) method.invoke(processor, zipCode, houses, populations);
        assertEquals(300, result);
    }

    @Test
    void testStrategyMethods_WithNullHouses() throws Exception {
        // Test case: Test strategy methods with null houses list
        int zipCode = 19104;

        housingReader = new TestHousingReader(new java.util.ArrayList<>());
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // Test calculateAverageMarketValue
        Method method1 = HousingProcessor.class.getDeclaredMethod(
                "calculateAverageMarketValue", 
                Integer.class, 
                List.class, 
                Map.class
        );
        method1.setAccessible(true);
        Integer result1 = (Integer) method1.invoke(processor, zipCode, null, new HashMap<>());
        assertEquals(0, result1);

        // Test calculateAverageLivableArea
        Method method2 = HousingProcessor.class.getDeclaredMethod(
                "calculateAverageLivableArea", 
                Integer.class, 
                List.class, 
                Map.class
        );
        method2.setAccessible(true);
        Integer result2 = (Integer) method2.invoke(processor, zipCode, null, new HashMap<>());
        assertEquals(0, result2);
    }

    @Test
    void testStrategyMethods_WithEmptyHouses() throws Exception {
        // Test case: Test strategy methods with empty houses list
        int zipCode = 19104;
        List<House> emptyHouses = new java.util.ArrayList<>();

        housingReader = new TestHousingReader(emptyHouses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // Test calculateAverageMarketValue
        Method method1 = HousingProcessor.class.getDeclaredMethod(
                "calculateAverageMarketValue", 
                Integer.class, 
                List.class, 
                Map.class
        );
        method1.setAccessible(true);
        Integer result1 = (Integer) method1.invoke(processor, zipCode, emptyHouses, new HashMap<>());
        assertEquals(0, result1);

        // Test calculateAverageLivableArea
        Method method2 = HousingProcessor.class.getDeclaredMethod(
                "calculateAverageLivableArea", 
                Integer.class, 
                List.class, 
                Map.class
        );
        method2.setAccessible(true);
        Integer result2 = (Integer) method2.invoke(processor, zipCode, emptyHouses, new HashMap<>());
        assertEquals(0, result2);
    }
}

