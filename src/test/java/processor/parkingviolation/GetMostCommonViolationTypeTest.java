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
 * Tests for the getMostCommonViolationType(int) method.
 * Each test method represents one test case.
 */
public class GetMostCommonViolationTypeTest {

    /**
     * Test case 1: Returns correct most common type
     */
    @Test
    public void testReturnsCorrectType() {
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
                19104, "METER EXPIRED", 40, "PA"
        ));
        violations.add(new ParkingViolation(
                "T004", "JKL012", "2024-01-04T13:00:00Z",
                19104, "DOUBLE PARKED", 30, "PA"
        ));

        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        String result = processor.getMostCommonViolationType(19104);

        assertEquals("METER EXPIRED", result);
    }

    /**
     * Test case 2: No violations returns null
     */
    @Test
    public void testNoViolations() {
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        String result = processor.getMostCommonViolationType(99999);

        assertNull(result);
    }
}