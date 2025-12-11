package processor.housing;

import org.junit.jupiter.api.Test;
import processor.HousingProcessor;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for HousingProcessor.PropertyValueSummary class.
 * Tests toString method and getter methods.
 */
class TestPropertyValueSummary {

    @Test
    void testPropertyValueSummary_ConstructorAndGetters() {
        // Test case: Constructor and getter methods
        HousingProcessor.PropertyValueSummary summary = 
                new HousingProcessor.PropertyValueSummary(100000, 500000, 300000);

        assertEquals(100000, summary.getMin());
        assertEquals(500000, summary.getMax());
        assertEquals(300000, summary.getMedian());
    }

    @Test
    void testPropertyValueSummary_ToString() {
        // Test case: toString method should return formatted string
        HousingProcessor.PropertyValueSummary summary = 
                new HousingProcessor.PropertyValueSummary(100000, 500000, 300000);

        String result = summary.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("Min: 100000"));
        assertTrue(result.contains("Max: 500000"));
        assertTrue(result.contains("Median: 300000"));
    }

    @Test
    void testPropertyValueSummary_WithZeroValues() {
        // Test case: PropertyValueSummary with zero values
        HousingProcessor.PropertyValueSummary summary = 
                new HousingProcessor.PropertyValueSummary(0, 0, 0);

        assertEquals(0, summary.getMin());
        assertEquals(0, summary.getMax());
        assertEquals(0, summary.getMedian());
        
        String result = summary.toString();
        assertTrue(result.contains("Min: 0"));
        assertTrue(result.contains("Max: 0"));
        assertTrue(result.contains("Median: 0"));
    }
}

