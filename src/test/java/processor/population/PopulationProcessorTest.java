package processor.population;

import org.junit.jupiter.api.Test;
import processor.PopulationProcessor;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PopulationProcessorTest {

    @Test
    public void testPopulation(){
        Map<String,Integer> testMap = new HashMap<>();
        testMap.put("10001", 500);
        testMap.put("10002", 700);
        testMap.put("10003", 1000);

        PopulationProcessor<String,Integer> p = new PopulationProcessor<>(testMap);

        int result = p.totalPopulation();
        assertEquals(2200,result);
    }

    @Test
    public void testConstructorThrowsOnNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new PopulationProcessor<String, Integer>(null));
    }

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

    @Test
    void testMapWithSingleEntry() {
        Map<String, Integer> map = new HashMap<>();
        map.put("60606", 999);

        PopulationProcessor<String, Integer> p = new PopulationProcessor<>(map);

        int result = p.totalPopulation();

        assertEquals(999, result);
    }

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

