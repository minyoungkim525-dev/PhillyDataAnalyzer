package processor.population;

import org.junit.jupiter.api.Test;
import processor.PopulationProcessor;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class testConstructorThrowsOnNull {
    @Test
    public void testConstructorThrowsOnNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new PopulationProcessor<String, Integer>(null));
    }
}
