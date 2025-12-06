package processor.housing;

import data.PopulationReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manual mock implementation of PopulationReader for testing.
 * Returns predefined data without reading from files.
 */
class TestPopulationReader implements PopulationReader {
    private final Map<Integer, Integer> populations;
    private final boolean shouldThrowException;
    
    public TestPopulationReader(Map<Integer, Integer> populations) {
        this.populations = populations != null ? new HashMap<>(populations) : new HashMap<>();
        this.shouldThrowException = false;
    }
    
    public TestPopulationReader(boolean shouldThrowException) {
        this.populations = new HashMap<>();
        this.shouldThrowException = shouldThrowException;
    }
    
    @Override
    public Map<Integer, Integer> readData() throws IOException {
        if (shouldThrowException) {
            throw new IOException("File not found");
        }
        return new HashMap<>(populations);
    }
}

