package processor.population;

import org.junit.jupiter.api.Test;
import processor.PopulationProcessor;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class testMapWithSingleEntry {
    @Test
    void testMapWithSingleEntry() {
        Map<String, Integer> map = new HashMap<>();
        map.put("60606", 999);

        PopulationProcessor<String, Integer> p = new PopulationProcessor<>(map);

        int result = p.totalPopulation();

        assertEquals(999, result);
    }

}
