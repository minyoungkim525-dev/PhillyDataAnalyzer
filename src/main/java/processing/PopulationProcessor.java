package processing;

import java.util.Iterator;
import java.util.Map;

/**
 * Processes ZIP code population data stored in a generic Map.
 * Uses Java generics to allow any key type and any numeric value type.
 *
 * @param <K> the type used for ZIP codes
 * @param <V> the numeric type used for population values
 */
public class PopulationProcessor<K, V extends Number> {

    private final Map<K, V> population;

    public PopulationProcessor(Map<K, V> population) {
        if (population == null) {
            throw new IllegalArgumentException("Population map must not be null.");
        }
        this.population = population;
    }

    /**
     * Calculates the total population by iterating over the map values
     * using the iterator pattern.
     *
     * @return the total population across all entries
     */
    public int totalPopulation() {
        int sum = 0;

        // Iterator pattern over entries
        Iterator<Map.Entry<K, V>> it = population.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> entry = it.next();
            V value = entry.getValue();

            if (value == null) {
                // Defensive Programming Element: catch ZIP codes with null population values but continue aggregating.
                System.out.println("Warning: ZIP code " + entry.getKey() + " has a null population value.");
                continue;
            }

            sum += value.intValue();
        }
        return sum;
    }
}
