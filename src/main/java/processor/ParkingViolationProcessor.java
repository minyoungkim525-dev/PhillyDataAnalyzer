package processor;

import common.ParkingViolation;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
}