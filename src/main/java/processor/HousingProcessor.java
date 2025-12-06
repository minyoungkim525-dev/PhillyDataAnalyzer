package processor;

import common.House;
import data.HousingReader;
import data.PopulationReader;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * HousingProcessor handles all housing-related calculations for menu options 3, 4, 5, and 6.
 * Implements Singleton, Strategy, and Iterator patterns.
 * Uses Generics, Varargs, Enums, Streams, Lambda expressions, and Threads.
 * Includes memoization for performance optimization.
 */
public class HousingProcessor {
    
    // DESIGN PATTERN: Singleton - ensures only one instance exists
    private static volatile HousingProcessor instance;
    private static final Object lock = new Object();
    
    private final HousingReader housingReader;
    private final PopulationReader populationReader;
    
    // MEMOIZATION: Cache for expensive calculations
    private final Map<String, Object> calculationCache = new ConcurrentHashMap<>();
    private final Map<Integer, List<House>> housesByZipCache = new ConcurrentHashMap<>();
    
    // DESIGN PATTERN: Strategy - different calculation strategies
    private final Map<CalculationType, CalculationStrategy<Integer, Integer>> strategies;
    
    // JAVA FEATURE: Enum for calculation types
    public enum CalculationType {
        AVERAGE_MARKET_VALUE,
        AVERAGE_LIVABLE_AREA,
        MARKET_VALUE_PER_CAPITA,
        PROPERTY_VALUE_SUMMARY
    }
    
    // DESIGN PATTERN: Strategy interface using Generics
    @FunctionalInterface
    public interface CalculationStrategy<T, R> {
        R calculate(T input, List<House> houses, Map<Integer, Integer> populations);
    }
    
    private HousingProcessor(HousingReader housingReader, PopulationReader populationReader) {
        this.housingReader = housingReader;
        this.populationReader = populationReader;
        
        // Initialize strategies
        this.strategies = new HashMap<>();
        this.strategies.put(CalculationType.AVERAGE_MARKET_VALUE, this::calculateAverageMarketValue);
        this.strategies.put(CalculationType.AVERAGE_LIVABLE_AREA, this::calculateAverageLivableArea);
        this.strategies.put(CalculationType.MARKET_VALUE_PER_CAPITA, this::calculateMarketValuePerCapita);
    }
    
    // DESIGN PATTERN: Singleton - thread-safe getInstance method
    public static HousingProcessor getInstance(HousingReader housingReader, PopulationReader populationReader) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new HousingProcessor(housingReader, populationReader);
                }
            }
        }
        return instance;
    }
    
    /**
     * Menu Option #3: Average residential market value for a ZIP Code
     */
    public int getAverageMarketValue(int zipCode) {
        String cacheKey = "avg_market_" + zipCode;
        
        // MEMOIZATION: Check cache first
        if (calculationCache.containsKey(cacheKey)) {
            return (Integer) calculationCache.get(cacheKey);
        }
        
        List<House> houses = getHousesByZipCode(zipCode);
        
        // JAVA FEATURE: Streams and Lambda expressions
        OptionalDouble average = houses.stream()
                .map(House::getMarket_value)
                .filter(Objects::nonNull)
                .filter(value -> value > 0)
                .mapToInt(Integer::intValue)
                .average();
        
        int result = average.isPresent() ? (int) Math.round(average.getAsDouble()) : 0;
        
        // MEMOIZATION: Cache the result
        calculationCache.put(cacheKey, result);
        return result;
    }
    
    /**
     * Menu Option #4: Average residential total livable area for a ZIP Code
     */
    public int getAverageLivableArea(int zipCode) {
        String cacheKey = "avg_livable_" + zipCode;
        
        // MEMOIZATION: Check cache first
        if (calculationCache.containsKey(cacheKey)) {
            return (Integer) calculationCache.get(cacheKey);
        }
        
        List<House> houses = getHousesByZipCode(zipCode);
        
        // JAVA FEATURE: Streams and Lambda expressions
        OptionalDouble average = houses.stream()
                .map(House::getTotal_livable_area)
                .filter(Objects::nonNull)
                .filter(area -> area > 0)
                .mapToInt(Integer::intValue)
                .average();
        
        int result = average.isPresent() ? (int) Math.round(average.getAsDouble()) : 0;
        
        // MEMOIZATION: Cache the result
        calculationCache.put(cacheKey, result);
        return result;
    }
    
    /**
     * Menu Option #5: Residential market value per capita for a ZIP Code
     */
    public int getMarketValuePerCapita(int zipCode) {
        String cacheKey = "market_per_capita_" + zipCode;
        
        // MEMOIZATION: Check cache first
        if (calculationCache.containsKey(cacheKey)) {
            return (Integer) calculationCache.get(cacheKey);
        }
        
        try {
            Map<Integer, Integer> populations = populationReader.readData();
            Integer population = populations.get(zipCode);
            
            if (population == null || population == 0) {
                calculationCache.put(cacheKey, 0);
                return 0;
            }
            
            List<House> houses = getHousesByZipCode(zipCode);
            
            // JAVA FEATURE: Streams and Lambda expressions
            int totalMarketValue = houses.stream()
                    .map(House::getMarket_value)
                    .filter(Objects::nonNull)
                    .filter(value -> value > 0)
                    .mapToInt(Integer::intValue)
                    .sum();
            
            int result = (int) Math.round((double) totalMarketValue / population);
            
            // MEMOIZATION: Cache the result
            calculationCache.put(cacheKey, result);
            return result;
        } catch (Exception e) {
            calculationCache.put(cacheKey, 0);
            return 0;
        }
    }
    
    /**
     * Menu Option #6: Property Value Summary for a ZIP Code
     * Returns: minimum, maximum, and median market value
     */
    public PropertyValueSummary getPropertyValueSummary(int zipCode) {
        String cacheKey = "property_summary_" + zipCode;
        
        // MEMOIZATION: Check cache first
        if (calculationCache.containsKey(cacheKey)) {
            return (PropertyValueSummary) calculationCache.get(cacheKey);
        }
        
        List<House> houses = getHousesByZipCode(zipCode);
        
        // JAVA FEATURE: Streams and Lambda expressions
        List<Integer> validMarketValues = houses.stream()
                .map(House::getMarket_value)
                .filter(Objects::nonNull)
                .filter(value -> value > 0)
                .sorted()
                .collect(Collectors.toList());
        
        if (validMarketValues.isEmpty()) {
            PropertyValueSummary summary = new PropertyValueSummary(0, 0, 0);
            calculationCache.put(cacheKey, summary);
            return summary;
        }
        
        int min = validMarketValues.get(0);
        int max = validMarketValues.get(validMarketValues.size() - 1);
        int median = calculateMedian(validMarketValues);
        
        PropertyValueSummary summary = new PropertyValueSummary(min, max, median);
        
        // MEMOIZATION: Cache the result
        calculationCache.put(cacheKey, summary);
        return summary;
    }
    
    /**
     * JAVA FEATURE: Generics - Generic method for filtering houses
     * JAVA FEATURE: Varargs - Accepts multiple predicates
     * This method demonstrates varargs usage for flexible filtering
     */
    @SafeVarargs
    public final List<House> filterHouses(List<House> houses, Predicate<House>... predicates) {
        // JAVA FEATURE: Streams and Lambda expressions
        Stream<House> stream = houses.stream();
        for (Predicate<House> predicate : predicates) {
            stream = stream.filter(predicate);
        }
        return stream.collect(Collectors.toList());
    }
    
    /**
     * Helper method to get houses by ZIP code with caching
     */
    private List<House> getHousesByZipCode(int zipCode) {
        // MEMOIZATION: Check cache for houses by ZIP
        if (housesByZipCache.containsKey(zipCode)) {
            return housesByZipCache.get(zipCode);
        }
        
        try {
            List<House> allHouses = housingReader.readData();
            
            // JAVA FEATURE: Streams and Lambda expressions
            List<House> houses = allHouses.stream()
                    .filter(house -> house.getZip_code() != null && house.getZip_code().equals(zipCode))
                    .collect(Collectors.toList());
            
            // MEMOIZATION: Cache the filtered houses
            housesByZipCache.put(zipCode, houses);
            return houses;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Calculate median from sorted list
     */
    private int calculateMedian(List<Integer> sortedValues) {
        int size = sortedValues.size();
        if (size % 2 == 0) {
            return (sortedValues.get(size / 2 - 1) + sortedValues.get(size / 2)) / 2;
        } else {
            return sortedValues.get(size / 2);
        }
    }
    
    /**
     * DESIGN PATTERN: Strategy - Calculate average market value using strategy
     */
    private Integer calculateAverageMarketValue(Integer zipCode, List<House> houses, Map<Integer, Integer> populations) {
        return getAverageMarketValue(zipCode);
    }
    
    /**
     * DESIGN PATTERN: Strategy - Calculate average livable area using strategy
     */
    private Integer calculateAverageLivableArea(Integer zipCode, List<House> houses, Map<Integer, Integer> populations) {
        return getAverageLivableArea(zipCode);
    }
    
    /**
     * DESIGN PATTERN: Strategy - Calculate market value per capita using strategy
     */
    private Integer calculateMarketValuePerCapita(Integer zipCode, List<House> houses, Map<Integer, Integer> populations) {
        return getMarketValuePerCapita(zipCode);
    }
    
    /**
     * DESIGN PATTERN: Iterator - Custom iterator for houses by ZIP code
     */
    public Iterator<House> getHouseIterator(int zipCode) {
        List<House> houses = getHousesByZipCode(zipCode);
        return houses.iterator();
    }
    
    /**
     * JAVA FEATURE: Threads - Parallel processing for batch calculations
     * JAVA FEATURE: Varargs - Accepts multiple ZIP codes
     */
    public Map<Integer, Integer> calculateAverageMarketValuesParallel(int... zipCodes) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Map<Integer, Integer> results = new ConcurrentHashMap<>();
        
        List<Future<?>> futures = new ArrayList<>();
        
        for (int zipCode : zipCodes) {
            futures.add(executor.submit(() -> {
                int value = getAverageMarketValue(zipCode);
                results.put(zipCode, value);
            }));
        }
        
        // Wait for all tasks to complete
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        executor.shutdown();
        return results;
    }
    
    /**
     * Data class for property value summary
     */
    public static class PropertyValueSummary {
        private final int min;
        private final int max;
        private final int median;
        
        public PropertyValueSummary(int min, int max, int median) {
            this.min = min;
            this.max = max;
            this.median = median;
        }
        
        public int getMin() {
            return min;
        }
        
        public int getMax() {
            return max;
        }
        
        public int getMedian() {
            return median;
        }
        
        @Override
        public String toString() {
            return String.format("Min: %d, Max: %d, Median: %d", min, max, median);
        }
    }
    
    /**
     * Clear all caches (useful for testing)
     */
    public void clearCache() {
        calculationCache.clear();
        housesByZipCache.clear();
    }
    
    /**
     * Reset the singleton instance (useful for testing)
     */
    public static void resetInstance() {
        synchronized (lock) {
            instance = null;
        }
    }
}
