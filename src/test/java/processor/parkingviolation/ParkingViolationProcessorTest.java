package processor;

import common.ParkingViolation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for ParkingViolationProcessor.
 * Each test creates its own test data.
 */
public class ParkingViolationProcessorTest {

    /**
     * Test that fines per capita is calculated correctly.
     * Formula: total fines / population
     */
    @Test
    public void testCalculateFinesPerCapita_BasicCalculation() {
        // Create test data
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        populations.put(19104, 1000);  // 1000 people

        // Add violations for 19104: $50 + $60 + $40 = $150 total
        violations.add(new ParkingViolation(
                "2024-01-01T10:00:00Z", 50, "METER EXPIRED",
                "ABC123", "PA", "T001", 19104
        ));
        violations.add(new ParkingViolation(
                "2024-01-02T11:00:00Z", 60, "DOUBLE PARKED",
                "DEF456", "PA", "T002", 19104
        ));
        violations.add(new ParkingViolation(
                "2024-01-03T12:00:00Z", 40, "METER EXPIRED",
                "GHI789", "PA", "T003", 19104
        ));

        // Create processor and calculate
        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        Map<Integer, Double> result = processor.calculateFinesPerCapita();

        // Verify: $150 / 1000 people = $0.15 per capita
        assertTrue(result.containsKey(19104), "19104 should be in results");
        assertEquals(0.15, result.get(19104), 0.0001, "19104 fines per capita should be 0.15");
    }

    /**
     * Test that only PA state violations are counted.
     */
    @Test
    public void testCalculateFinesPerCapita_OnlyPAStateIncluded() {
        // Create test data
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        populations.put(19104, 1000);

        // Add PA violation: $50
        violations.add(new ParkingViolation(
                "2024-01-01T10:00:00Z", 50, "METER EXPIRED",
                "ABC123", "PA", "T001", 19104
        ));

        // Add NJ violation: $100 (should be ignored)
        violations.add(new ParkingViolation(
                "2024-01-02T11:00:00Z", 100, "METER EXPIRED",
                "DEF456", "NJ", "T002", 19104
        ));

        // Create processor and calculate
        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        Map<Integer, Double> result = processor.calculateFinesPerCapita();

        // Verify: Only $50 (PA) counted, not $150 (PA + NJ)
        assertEquals(0.05, result.get(19104), 0.0001, "Should only count PA violations");
    }

    /**
     * Test that violations with null ZIP code are excluded.
     */
    @Test
    public void testCalculateFinesPerCapita_NullZipExcluded() {
        // Create test data
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        populations.put(19104, 1000);

        // Add violation with null ZIP
        violations.add(new ParkingViolation(
                "2024-01-01T10:00:00Z", 50, "METER EXPIRED",
                "ABC123", "PA", "T001", null
        ));

        // Create processor and calculate
        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        Map<Integer, Double> result = processor.calculateFinesPerCapita();

        // Verify: null should not be in results
        assertFalse(result.containsKey(null), "Null ZIP should not be in results");
        assertTrue(result.isEmpty(), "Result should be empty");
    }

    /**
     * Test that ZIP codes with zero population are excluded.
     */
    @Test
    public void testCalculateFinesPerCapita_ZeroPopulationExcluded() {
        // Create test data
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        populations.put(19102, 0);  // Zero population

        // Add violation
        violations.add(new ParkingViolation(
                "2024-01-01T10:00:00Z", 50, "METER EXPIRED",
                "ABC123", "PA", "T001", 19102
        ));

        // Create processor and calculate
        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        Map<Integer, Double> result = processor.calculateFinesPerCapita();

        // Verify: ZIP with zero population should be excluded
        assertFalse(result.containsKey(19102), "ZIP with zero population should be excluded");
    }

    /**
     * Test that ZIP codes not in population map are excluded.
     */
    @Test
    public void testCalculateFinesPerCapita_MissingPopulationExcluded() {
        // Create test data
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        // 19105 NOT in population map
        populations.put(19104, 1000);  // Different ZIP

        // Add violation for 19105
        violations.add(new ParkingViolation(
                "2024-01-01T10:00:00Z", 50, "METER EXPIRED",
                "ABC123", "PA", "T001", 19105
        ));

        // Create processor and calculate
        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        Map<Integer, Double> result = processor.calculateFinesPerCapita();

        // Verify: ZIP not in population map should be excluded
        assertFalse(result.containsKey(19105), "ZIP not in population map should be excluded");
    }

    /**
     * Test getting violation types for a specific ZIP code.
     */
    @Test
    public void testGetViolationTypesForZip_CountsCorrect() {
        // Create test data
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        populations.put(19104, 1000);

        // Add 2 METER EXPIRED and 1 DOUBLE PARKED for 19104
        violations.add(new ParkingViolation(
                "2024-01-01T10:00:00Z", 50, "METER EXPIRED",
                "ABC123", "PA", "T001", 19104
        ));
        violations.add(new ParkingViolation(
                "2024-01-02T11:00:00Z", 60, "METER EXPIRED",
                "DEF456", "PA", "T002", 19104
        ));
        violations.add(new ParkingViolation(
                "2024-01-03T12:00:00Z", 40, "DOUBLE PARKED",
                "GHI789", "PA", "T003", 19104
        ));

        // Create processor
        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        Map<String, Integer> result = processor.getViolationTypesForZip(19104);

        // Verify counts
        assertEquals(2, result.get("METER EXPIRED"), "METER EXPIRED count should be 2");
        assertEquals(1, result.get("DOUBLE PARKED"), "DOUBLE PARKED count should be 1");
    }

    /**
     * Test getting violation types for ZIP with no violations.
     */
    @Test
    public void testGetViolationTypesForZip_NoViolations() {
        // Create test data
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        populations.put(19104, 1000);

        // Add violation for different ZIP
        violations.add(new ParkingViolation(
                "2024-01-01T10:00:00Z", 50, "METER EXPIRED",
                "ABC123", "PA", "T001", 19103
        ));

        // Create processor
        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        Map<String, Integer> result = processor.getViolationTypesForZip(99999);  // ZIP with no violations

        // Verify
        assertTrue(result.isEmpty(), "Result should be empty for ZIP with no violations");
    }

    /**
     * Test getting most common violation type.
     */
    @Test
    public void testGetMostCommonViolationType_ReturnsCorrectType() {
        // Create test data
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        populations.put(19104, 1000);

        // Add 3 METER EXPIRED and 1 DOUBLE PARKED
        violations.add(new ParkingViolation(
                "2024-01-01T10:00:00Z", 50, "METER EXPIRED",
                "ABC123", "PA", "T001", 19104
        ));
        violations.add(new ParkingViolation(
                "2024-01-02T11:00:00Z", 60, "METER EXPIRED",
                "DEF456", "PA", "T002", 19104
        ));
        violations.add(new ParkingViolation(
                "2024-01-03T12:00:00Z", 40, "METER EXPIRED",
                "GHI789", "PA", "T003", 19104
        ));
        violations.add(new ParkingViolation(
                "2024-01-04T13:00:00Z", 30, "DOUBLE PARKED",
                "JKL012", "PA", "T004", 19104
        ));

        // Create processor
        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        String result = processor.getMostCommonViolationType(19104);

        // Verify: METER EXPIRED appears 3 times (most common)
        assertEquals("METER EXPIRED", result, "Most common type should be METER EXPIRED");
    }

    /**
     * Test getting most common violation for ZIP with no violations.
     */
    @Test
    public void testGetMostCommonViolationType_NoViolations() {
        // Create test data
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        // Create processor with empty violations
        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        String result = processor.getMostCommonViolationType(99999);

        // Verify
        assertNull(result, "Should return null for ZIP with no violations");
    }

    /**
     * Test with completely empty data.
     */
    @Test
    public void testCalculateFinesPerCapita_EmptyViolations() {
        // Create empty test data
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();
        populations.put(19104, 1000);

        // Create processor
        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        Map<Integer, Double> result = processor.calculateFinesPerCapita();

        // Verify
        assertTrue(result.isEmpty(), "Result should be empty with no violations");
    }
}