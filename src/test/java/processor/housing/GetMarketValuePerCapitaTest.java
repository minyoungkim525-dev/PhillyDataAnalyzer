package processor.housing;

import common.House;
import data.HousingReader;
import data.PopulationReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import processor.HousingProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for getMarketValuePerCapita(int zipCode) method.
 */
public class GetMarketValuePerCapitaTest {

    @AfterEach
    public void tearDown() {
        HousingProcessor.resetInstance();
    }

    @Test
    public void testBasicCalculation() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));
        houses.add(new House(19104, 200000, 2000));

        Map<Integer, Integer> populations = new HashMap<>();
        populations.put(19104, 1000);  // 1000 people

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(populations);

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        // Total: 300000 / 1000 = 300
        int result = processor.getMarketValuePerCapita(19104);

        assertEquals(300, result);
    }

    @Test
    public void testZeroPopulation() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));

        Map<Integer, Integer> populations = new HashMap<>();
        populations.put(19104, 0);  // Zero population

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(populations);

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        int result = processor.getMarketValuePerCapita(19104);

        assertEquals(0, result);
    }

    @Test
    public void testNullPopulation() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));

        Map<Integer, Integer> populations = new HashMap<>();
        // 19104 not in map

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(populations);

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        int result = processor.getMarketValuePerCapita(19104);

        assertEquals(0, result);
    }

    @Test
    public void testNoHouses() throws IOException {
        List<House> houses = new ArrayList<>();

        Map<Integer, Integer> populations = new HashMap<>();
        populations.put(19104, 1000);

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(populations);

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        int result = processor.getMarketValuePerCapita(19104);

        assertEquals(0, result);
    }

    @Test
    public void testFiltersInvalidValues() throws IOException {
        List<House> houses = new ArrayList<>();
        houses.add(new House(19104, 100000, 1000));
        houses.add(new House(19104, null, 2000));     // Null
        houses.add(new House(19104, 0, 3000));        // Zero
        houses.add(new House(19104, 200000, 4000));

        Map<Integer, Integer> populations = new HashMap<>();
        populations.put(19104, 1000);

        HousingReader housingReader = new MockHousingReader(houses);
        PopulationReader populationReader = new MockPopulationReader(populations);

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        // Only counts 100000 + 200000 = 300000 / 1000 = 300
        int result = processor.getMarketValuePerCapita(19104);

        assertEquals(300, result);
    }

    @Test
    public void testException() throws IOException {
        HousingReader housingReader = new ErrorHousingReader();
        PopulationReader populationReader = new ErrorPopulationReader();

        HousingProcessor processor = HousingProcessor.getInstance(housingReader, populationReader);

        int result = processor.getMarketValuePerCapita(19104);

        assertEquals(0, result);  // Returns 0 on exception
    }

    private static class MockHousingReader extends HousingReader {
        private final List<House> houses;

        public MockHousingReader(List<House> houses) {
            super("dummy.csv");
            this.houses = houses;
        }

        @Override
        public List<House> readData() {
            return houses;
        }
    }

    private static class MockPopulationReader implements PopulationReader {
        private final Map<Integer, Integer> populations;

        public MockPopulationReader(Map<Integer, Integer> populations) {
            this.populations = populations;
        }

        @Override
        public Map<Integer, Integer> readData() {
            return populations;
        }
    }

    private static class ErrorHousingReader extends HousingReader {
        public ErrorHousingReader() {
            super("dummy.csv");
        }

        @Override
        public List<House> readData() throws IOException {
            throw new IOException("Test exception");
        }
    }

    private static class ErrorPopulationReader implements PopulationReader {
        @Override
        public Map<Integer, Integer> readData() throws IOException {
            throw new IOException("Test exception");
        }
    }
}