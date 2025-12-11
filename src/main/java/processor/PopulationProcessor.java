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
     * Menu Option #1: Total population for all ZIP Codes..
     */
    public int totalPopulation() {
        int sum = 0;

        for (Map.Entry<K, V> entry : population.entrySet()) {
            V value = entry.getValue();

            if (value == null) {
                System.out.println("Warning: ZIP code " + entry.getKey() +
                        " has a null population value.");
                continue;
            }

            sum += value.intValue();
        }

        return sum;
    }
}
