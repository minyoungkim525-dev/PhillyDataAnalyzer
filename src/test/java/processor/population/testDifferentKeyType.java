package processor.population;

import org.junit.jupiter.api.Test;
import processor.PopulationProcessor;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class testDifferentKeyType {
    @Test
    void testDifferentKeyType(){
        Map<Integer,Integer> map = new HashMap<>();
        map.put(1,100);
        map.put(2,100);
        map.put(3,100);

        PopulationProcessor<Integer, Integer> p = new PopulationProcessor<>(map);

        int result = p.totalPopulation();
        assertEquals(300,result);
    }
}
