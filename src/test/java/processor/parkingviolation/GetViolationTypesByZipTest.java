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
 * Tests for the getViolationTypesByZip() method.
 * Each test method represents one test case.
 */
public class GetViolationTypesByZipTest {

    /**
     * Test case 1: Returns data for all ZIP codes
     */
    @Test
    public void testReturnsAllZips() {
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        violations.add(new ParkingViolation(
                "T001", "ABC123", "2024-01-01T10:00:00Z",
                19104, "METER EXPIRED", 50, "PA"
        ));
        violations.add(new ParkingViolation(
                "T002", "DEF456", "2024-01-02T11:00:00Z",
                19103, "DOUBLE PARKED", 60, "PA"
        ));

        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        Map<Integer, Map<String, Integer>> result = processor.getViolationTypesByZip();

        assertTrue(result.containsKey(19104));
        assertTrue(result.containsKey(19103));
        assertEquals(1, result.get(19104).get("METER EXPIRED"));
        assertEquals(1, result.get(19103).get("DOUBLE PARKED"));
    }

    /**
     * Test case 2: Null ZIP codes excluded
     */
    @Test
    public void testNullZipExcluded() {
        List<ParkingViolation> violations = new ArrayList<>();
        Map<Integer, Integer> populations = new HashMap<>();

        violations.add(new ParkingViolation(
                "T001", "ABC123", "2024-01-01T10:00:00Z",
                null, "METER EXPIRED", 50, "PA"
        ));

        ParkingViolationProcessor processor = new ParkingViolationProcessor(violations, populations);
        Map<Integer, Map<String, Integer>> result = processor.getViolationTypesByZip();

        assertTrue(result.isEmpty());
    }
}