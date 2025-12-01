package processor.population;

import org.junit.jupiter.api.Test;
import processor.PopulationProcessor;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class testDifferentValueType {
    @Test
    void testDifferentValueType() {
        Map<String, Double> map = new HashMap<>();
        map.put("10001", 100.0);
        map.put("10002", 100.0);
        map.put("10003", 100.0);

        PopulationProcessor<String, Double> p = new PopulationProcessor<>(map);

        int result = p.totalPopulation();
        assertEquals(300, result);
    }
}
