package processor.parkingviolation;

import common.ParkingViolation;
import org.junit.jupiter.api.Test;
import processor.ParkingViolationProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the calculateFinesPerCapita() method.
 * Each test method represents one test case.
 */
public class CalculateFinesPerCapitaTest {

    /**
     * Test case 1: Basic calculation - total fines / population
     */
    @Test
    public void testBasicCalculation() {
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        populations.put(19104, 1000);

        violations.add(new ParkingViolation(
                "T001", "ABC123", "2024-01-01T10:00:00Z",
                19104, "METER EXPIRED", 50, "PA"
        ));
        violations.add(new ParkingViolation(
                "T002", "DEF456", "2024-01-02T11:00:00Z",
                19104, "DOUBLE PARKED", 60, "PA"
        ));
        violations.add(new ParkingViolation(
                "T003", "GHI789", "2024-01-03T12:00:00Z",
                19104, "METER EXPIRED", 40, "PA"
        ));

        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        Map<Integer, Double> result = processor.calculateFinesPerCapita();

        assertTrue(result.containsKey(19104));
        assertEquals(0.15, result.get(19104), 0.0001);
    }

    /**
     * Test case 2: Only PA state violations counted
     */
    @Test
    public void testOnlyPAStateIncluded() {
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        populations.put(19104, 1000);

        violations.add(new ParkingViolation(
                "T001", "ABC123", "2024-01-01T10:00:00Z",
                19104, "METER EXPIRED", 50, "PA"
        ));
        violations.add(new ParkingViolation(
                "T002", "DEF456", "2024-01-02T11:00:00Z",
                19104, "METER EXPIRED", 100, "NJ"
        ));

        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        Map<Integer, Double> result = processor.calculateFinesPerCapita();

        assertEquals(0.05, result.get(19104), 0.0001);
    }

    /**
     * Test case 3: Null ZIP codes excluded
     */
    @Test
    public void testNullZipExcluded() {
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        populations.put(19104, 1000);

        violations.add(new ParkingViolation(
                "T001", "ABC123", "2024-01-01T10:00:00Z",
                null, "METER EXPIRED", 50, "PA"
        ));

        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        Map<Integer, Double> result = processor.calculateFinesPerCapita();

        assertTrue(result.isEmpty());
    }

    /**
     * Test case 4: Zero population excluded
     */
    @Test
    public void testZeroPopulationExcluded() {
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        populations.put(19102, 0);

        violations.add(new ParkingViolation(
                "T001", "ABC123", "2024-01-01T10:00:00Z",
                19102, "METER EXPIRED", 50, "PA"
        ));

        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        Map<Integer, Double> result = processor.calculateFinesPerCapita();

        assertFalse(result.containsKey(19102));
    }

    /**
     * Test case 5: Missing population excluded
     */
    @Test
    public void testMissingPopulationExcluded() {
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        populations.put(19104, 1000);

        violations.add(new ParkingViolation(
                "T001", "ABC123", "2024-01-01T10:00:00Z",
                19105, "METER EXPIRED", 50, "PA"
        ));

        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        Map<Integer, Double> result = processor.calculateFinesPerCapita();

        assertFalse(result.containsKey(19105));
    }

    /**
     * Test case 6: Empty violations list
     */
    @Test
    public void testEmptyViolations() {
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();
        populations.put(19104, 1000);

        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        Map<Integer, Double> result = processor.calculateFinesPerCapita();

        assertTrue(result.isEmpty());
    }
}