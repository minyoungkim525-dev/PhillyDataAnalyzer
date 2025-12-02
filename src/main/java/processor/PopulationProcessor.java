package processor;

import java.util.Map;

public class PopulationProcessor {

    private final Map<Integer, Integer> population;

    public PopulationProcessor(Map<Integer, Integer> population) {
        if (population == null) {
            throw new IllegalArgumentException("Population map must not be null.");
        }
        this.population = population;
    }

    /**
     * Menu Option #1: Total population for all ZIP Codes.
     * Uses a simple enhanced for-loop (no iterator, no generics).
     */
    public int totalPopulation() {
        int sum = 0;

        for (Map.Entry<Integer, Integer> entry : population.entrySet()) {
            Integer value = entry.getValue();

            if (value == null) {
                System.out.println("Warning: ZIP code " + entry.getKey() + 
                                   " has a null population value.");
                continue;
            }

            sum += value;
        }

        return sum;
    }
}
