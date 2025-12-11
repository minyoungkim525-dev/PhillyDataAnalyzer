package processor.housing;

import common.House;
import data.HousingReader;
import data.PopulationReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processor.HousingProcessor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for HousingProcessor Strategy pattern implementation.
 * Tests the CalculationType enum and strategy pattern usage.
 */
class TestHousingProcessorStrategyPattern {

    private HousingReader housingReader;
    private PopulationReader populationReader;
    private HousingProcessor processor;

    @BeforeEach
    void setUp() {
        HousingProcessor.resetInstance();
    }

    @Test
    void testCalculationTypeEnum() {
        // Test case: Verify all CalculationType enum values exist
        HousingProcessor.CalculationType[] types = HousingProcessor.CalculationType.values();
        
        assertTrue(types.length >= 3);
        assertTrue(Arrays.asList(types).contains(HousingProcessor.CalculationType.AVERAGE_MARKET_VALUE));
        assertTrue(Arrays.asList(types).contains(HousingProcessor.CalculationType.AVERAGE_LIVABLE_AREA));
        assertTrue(Arrays.asList(types).contains(HousingProcessor.CalculationType.MARKET_VALUE_PER_CAPITA));
    }

    @Test
    void testStrategyPattern_IndirectUsage() {
        // Test case: Strategy methods are called indirectly through public methods
        // This tests that the strategy pattern is working correctly
        int zipCode = 19104;
        List<House> houses = Arrays.asList(
                new House(zipCode, 100000, 1000),
                new House(zipCode, 200000, 2000)
        );

        housingReader = new TestHousingReader(houses);
        populationReader = new TestPopulationReader(new HashMap<>());
        processor = HousingProcessor.getInstance(housingReader, populationReader);
        processor.clearCache();

        // These calls indirectly use the strategy pattern methods
        int avgMarket = processor.getAverageMarketValue(zipCode);
        int avgLivable = processor.getAverageLivableArea(zipCode);
        int perCapita = processor.getMarketValuePerCapita(zipCode);

        assertEquals(150000, avgMarket);
        assertEquals(1500, avgLivable);
        assertEquals(0, perCapita); // No population data
    }
}

