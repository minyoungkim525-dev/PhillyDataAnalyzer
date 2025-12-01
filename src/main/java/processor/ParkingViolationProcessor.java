package processor;

import common.ParkingViolation;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

public class ParkingViolationProcessor {

    private List<ParkingViolation> violations;
    private Map<Integer, Integer> populations;


    public ParkingViolationProcessor(List<ParkingViolation> violations, Map<Integer, Integer> populations) {
        this.violations = violations;
        this.populations = populations;
    }


    public Map<Integer, Double> calculateFinesPerCapita() {
        Map<Integer, Integer> totalFinesByZip = new TreeMap<>();

        for (ParkingViolation violation : violations) {
            // Filter: ignore if ZIP is null
            if (violation.getZip_code() == null) {
                continue;
            }

            // Filter: ignore if state is not "PA"
            if (!"PA".equals(violation.getState())) {
                continue;
            }

            Integer zip_code = violation.getZip_code();
            int fine = violation.getFine();

            // Add fine to total for this ZIP code
            totalFinesByZip.put(zip_code, totalFinesByZip.getOrDefault(zip_code, 0) + fine);
        }

        // Step 2: Calculate fines per capita for each ZIP code
        Map<Integer, Double> finesPerCapita = new TreeMap<>();

        for (Map.Entry<Integer, Integer> entry : totalFinesByZip.entrySet()) {
            Integer zip_code = entry.getKey();
            Integer totalFines = entry.getValue();

            // Get population for this ZIP code
            Integer population = populations.get(zip_code);

            // Filter: skip if population is null or zero
            if (population == null || population == 0) {
                continue;
            }

            // Filter: skip if total fines is zero
            if (totalFines == 0) {
                continue;
            }

            // Calculate fines per capita
            double perCapita = (double) totalFines / population;
            finesPerCapita.put(zip_code, perCapita);
        }

        return finesPerCapita;
    }


    // Gets violation type counts for each ZIP code.
    public Map<Integer, Map<String, Integer>> getViolationTypesByZip() {
        // TreeMap keeps ZIP codes sorted
        Map<Integer, Map<String, Integer>> violationsByZip = new TreeMap<>();

        for (ParkingViolation violation : violations) {
            // Skip if ZIP code is null
            if (violation.getZip_code() == null) {
                continue;
            }

            Integer zip_code = violation.getZip_code();
            String violationType = violation.getViolation();

            // Get or create the inner map for this ZIP code
            Map<String, Integer> typeCounts = violationsByZip.get(zip_code);
            if (typeCounts == null) {
                typeCounts = new HashMap<>();
                violationsByZip.put(zip_code, typeCounts);
            }

            // Increment count for this violation type
            typeCounts.put(violationType, typeCounts.getOrDefault(violationType, 0) + 1);
        }

        return violationsByZip;
    }

    // Gets violation type counts for a specific ZIP code.
    public Map<String, Integer> getViolationTypesForZip(int zipCode) {
        Map<String, Integer> typeCounts = new HashMap<>();

        for (ParkingViolation violation : violations) {
            // Only count violations for the specified ZIP code
            if (violation.getZip_code() != null && violation.getZip_code() == zipCode) {
                String violationType = violation.getViolation();
                typeCounts.put(violationType, typeCounts.getOrDefault(violationType, 0) + 1);
            }
        }

        return typeCounts;
    }


    // Gets the most common violation type for a specific ZIP code.
    public String getMostCommonViolationType(int zipCode) {
        Map<String, Integer> typeCounts = getViolationTypesForZip(zipCode);

        if (typeCounts.isEmpty()) {
            return null;
        }

        // Find the violation type with the highest count
        String mostCommon = null;
        int maxCount = 0;

        for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostCommon = entry.getKey();
            }
        }

        return mostCommon;
    }
}