package processor.housing;

import data.HousingReader;
import data.PopulationReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processor.HousingProcessor;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for HousingProcessor null checks and IllegalStateException handling.
 * Tests constructor and getInstance null validation.
 */
class TestHousingProcessorNullChecks {

    private HousingReader validHousingReader;
    private PopulationReader validPopulationReader;

    @BeforeEach
    void setUp() {
        HousingProcessor.resetInstance();
        validHousingReader = new TestHousingReader(new java.util.ArrayList<>());
        validPopulationReader = new TestPopulationReader(new HashMap<>());
    }

    @Test
    void testGetInstance_WithNullHousingReader() {
        // Test case: getInstance should throw IllegalStateException when HousingReader is null
        assertThrows(IllegalStateException.class, () -> {
            HousingProcessor.getInstance(null, validPopulationReader);
        });
    }

    @Test
    void testGetInstance_WithNullPopulationReader() {
        // Test case: getInstance should throw IllegalStateException when PopulationReader is null
        assertThrows(IllegalStateException.class, () -> {
            HousingProcessor.getInstance(validHousingReader, null);
        });
    }

    @Test
    void testGetInstance_WithBothNull() {
        // Test case: getInstance should throw IllegalStateException when both are null
        assertThrows(IllegalStateException.class, () -> {
            HousingProcessor.getInstance(null, null);
        });
    }

    @Test
    void testGetInstance_ValidReaders() {
        // Test case: getInstance should work with valid readers
        HousingProcessor processor = HousingProcessor.getInstance(validHousingReader, validPopulationReader);
        assertNotNull(processor);
    }
}

