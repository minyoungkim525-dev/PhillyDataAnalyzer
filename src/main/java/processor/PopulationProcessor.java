package processor;

import java.util.Map;

public class PopulationProcessor<K, V extends Number> {

    private final Map<K, V> population;

    public PopulationProcessor(Map<K, V> population) {
        if (population == null) {
            throw new IllegalArgumentException("Population map must not be null.");
        }
        this.population = population;
    }

    /**
     * Menu Option #1: Total population for all ZIP Codes.
     * Works for any numeric type (Integer, Long, Double, etc.)
     */
    public int totalPopulation() {
        if (population == null) {
            throw new IllegalStateException("Population map is not initialized.");
        }
        int sum = 0;

        for (Map.Entry<K, V> entry : population.entrySet()) {
            if (entry == null) {
                continue;
            }
            V value = entry.getValue();

            if (value == null) {
                System.out.println("Warning: ZIP code " + entry.getKey()
                        + " has a null population value.");
                continue;
            }

            // Use Number.intValue() to support any numeric V
            sum += value.intValue();
        }

        return sum;
    }
}
