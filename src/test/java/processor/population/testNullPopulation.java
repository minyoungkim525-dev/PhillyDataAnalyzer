package processor.population;

import org.junit.jupiter.api.Test;
import processor.PopulationProcessor;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class testNullPopulation {
    @Test
    public void testNullPopulation(){
        Map<String,Integer> testMap = new HashMap<>();
        testMap.put("10001", 500);
        testMap.put("10002", 700);
        testMap.put("10003", 1000);

        testMap.put("1004", null);

        PopulationProcessor<String,Integer> p = new PopulationProcessor<>(testMap);

        int result = p.totalPopulation();

        assertEquals(2200,result);
    }
}
