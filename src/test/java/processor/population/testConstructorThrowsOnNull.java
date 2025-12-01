package processor.population;

import org.junit.jupiter.api.Test;
import processor.PopulationProcessor;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class testConstructorThrowsOnNull {
    @Test
    public void testConstructorThrowsOnNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new PopulationProcessor<String, Integer>(null));
    }
}
