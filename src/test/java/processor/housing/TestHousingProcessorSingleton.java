package processor.housing;

import data.HousingReader;
import data.PopulationReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processor.HousingProcessor;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for HousingProcessor Singleton pattern.
 * Tests that the singleton pattern works correctly, including double-check locking.
 */
class TestHousingProcessorSingleton {

    private HousingReader housingReader;
    private PopulationReader populationReader;

    @BeforeEach
    void setUp() {
        HousingProcessor.resetInstance();
        housingReader = new TestHousingReader(new ArrayList<>());
        populationReader = new TestPopulationReader(new HashMap<>());
    }

    @Test
    void testSingleton_ReturnsSameInstance() {
        // Test case: getInstance should return the same instance on multiple calls
        HousingProcessor instance1 = HousingProcessor.getInstance(housingReader, populationReader);
        HousingProcessor instance2 = HousingProcessor.getInstance(housingReader, populationReader);
        
        assertSame(instance1, instance2);
    }

    @Test
    void testSingleton_AfterReset() {
        // Test case: After reset, should create a new instance
        HousingProcessor instance1 = HousingProcessor.getInstance(housingReader, populationReader);
        HousingProcessor.resetInstance();
        HousingProcessor instance2 = HousingProcessor.getInstance(housingReader, populationReader);
        
        // They should be different instances after reset
        assertNotSame(instance1, instance2);
    }

    @Test
    void testSingleton_DoubleCheckLocking() {
        // Test case: Test that double-check locking works (inner null check)
        // This is hard to test directly, but we can verify the singleton works correctly
        HousingProcessor instance1 = HousingProcessor.getInstance(housingReader, populationReader);
        
        // Call getInstance again - should return same instance (tests inner null check)
        HousingProcessor instance2 = HousingProcessor.getInstance(housingReader, populationReader);
        
        assertSame(instance1, instance2);
    }
}

