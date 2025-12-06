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
 * Tests for the getViolationTypesForZip(int) method.
 * Each test method represents one test case.
 */
public class GetViolationTypesForZipTest {

    /**
     * Test case 1: Correct counts for multiple violation types
     */
    @Test
    public void testCountsCorrect() {
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        populations.put(19104, 1000);

        violations.add(new ParkingViolation(
                "T001", "ABC123", "2024-01-01T10:00:00Z",
                19104, "METER EXPIRED", 50, "PA"
        ));
        violations.add(new ParkingViolation(
                "T002", "DEF456", "2024-01-02T11:00:00Z",
                19104, "METER EXPIRED", 60, "PA"
        ));
        violations.add(new ParkingViolation(
                "T003", "GHI789", "2024-01-03T12:00:00Z",
                19104, "DOUBLE PARKED", 40, "PA"
        ));

        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        Map<String, Integer> result = processor.getViolationTypesForZip(19104);

        assertEquals(2, result.get("METER EXPIRED"));
        assertEquals(1, result.get("DOUBLE PARKED"));
    }

    /**
     * Test case 2: No violations for ZIP code
     */
    @Test
    public void testNoViolations() {
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        populations.put(19104, 1000);

        violations.add(new ParkingViolation(
                "T001", "ABC123", "2024-01-01T10:00:00Z",
                19103, "METER EXPIRED", 50, "PA"
        ));

        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        Map<String, Integer> result = processor.getViolationTypesForZip(99999);

        assertTrue(result.isEmpty());
    }
}