package processor.housing;

import common.House;
import data.HousingReader;

import java.io.IOException;
import java.util.List;

/**
 * Manual mock implementation of HousingReader for testing.
 * Returns predefined data without reading from files.
 */
class TestHousingReader extends HousingReader {
    private final List<House> houses;
    private final boolean shouldThrowException;
    
    public TestHousingReader(List<House> houses) {
        super("test-file.csv"); // Call parent constructor with dummy filename
        this.houses = houses;
        this.shouldThrowException = false;
    }
    
    public TestHousingReader(boolean shouldThrowException) {
        super("test-file.csv");
        this.houses = null;
        this.shouldThrowException = shouldThrowException;
    }
    
    @Override
    public java.util.List<House> readData() throws IOException {
        if (shouldThrowException) {
            throw new IOException("File not found");
        }
        return houses != null ? houses : new java.util.ArrayList<>();
    }
}

