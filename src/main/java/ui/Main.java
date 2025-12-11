package ui;

import common.House;
import common.ParkingViolation;
import data.*;
import org.json.simple.parser.ParseException;
import processor.HousingProcessor;
import processor.ParkingViolationProcessor;
import processor.PopulationProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main class for PhillyDataAnalyzer application.
 * Implements Strategy pattern for menu option handling.
 */
public class Main {

    // DESIGN PATTERN: Strategy - Interface for menu option handlers
    @FunctionalInterface
    private interface MenuStrategy {
        void execute(ParkingViolationProcessor violationProcessor,
                    PopulationProcessor populationProcessor,
                    HousingProcessor housingProcessor,
                    Scanner scanner);
    }

    // DESIGN PATTERN: Strategy - Map of menu options to their strategies
    private static final Map<String, MenuStrategy> menuStrategies = initializeStrategies();
    
    private static Map<String, MenuStrategy> initializeStrategies() {
        Map<String, MenuStrategy> strategies = new HashMap<>();
        strategies.put("1", Main::handleTotalPopulation);
        strategies.put("2", Main::handleFinesPerCapita);
        strategies.put("3", Main::handleAverageResidentialMarket);
        strategies.put("4", Main::handleLivableArea);
        strategies.put("5", Main::handlePerCapitalResidentialValue);
        strategies.put("6", Main::handlePropertyValueSummary);
        strategies.put("7", Main::handleMostCommonViolation);
        return strategies;
    }

    public static void main(String args[]){
        if(args.length != 4){ // Error message 1
            System.out.println("Invalid number of arguments.");
            return;
        }

        String format = args[0];
        if(!format.equals("csv") && !format.equals("json")){ // Error message 2
            System.out.println("Error: Format should be listed as either \"json\" or \"csv\".");
            return;
        }
        String violationsFile = args[1];
        String propertiesFile = args[2];
        String populationFile = args[3];

        if (!canReadFile(violationsFile)) { //Error message 3.1
            System.out.println("Error: Cannot open parking violations file: " + violationsFile);
            return;
        }
        if (!canReadFile(propertiesFile)) { // Error message 3.2
            System.out.println("Error: Cannot open properties file: " + propertiesFile);
            return;
        }
        if (!canReadFile(populationFile)) { // Error message 3.3
            System.out.println("Error: Cannot open population file: " + populationFile);
            return;
        }

        // Read all data
        try {
            // Read parking violations based on format
            List<ParkingViolation> violations;
            if (format.equals("csv")) {
                ParkingViolationCSVReader csvReader = new ParkingViolationCSVReader(violationsFile);
                violations = csvReader.readData();
            } else {
                ParkingViolationJSONReader jsonReader = new ParkingViolationJSONReader(violationsFile);
                violations = jsonReader.readData();
            }
            if (violations == null) {
                throw new IllegalStateException("Failed to read parking violations data.");
            }

            // Read properties
            HousingReader housingReader = new HousingReader(propertiesFile);
            List<House> houses = housingReader.readData();
            if (houses == null) {
                throw new IllegalStateException("Failed to read housing data.");
            }

            // Read population
            PopulationFileReader popReader = new PopulationFileReader(populationFile);
            Map<Integer, Integer> populations = popReader.readData();
            if (populations == null) {
                throw new IllegalStateException("Failed to read population data.");
            }

            // Create processors
            PopulationProcessor populationProcessor = new PopulationProcessor(populations);
            ParkingViolationProcessor violationProcessor = new ParkingViolationProcessor(violations, populations);
            HousingProcessor housingProcessor = HousingProcessor.getInstance(housingReader, popReader);

            // Validate processors are not null
            if (populationProcessor == null) {
                throw new IllegalStateException("PopulationProcessor could not be initialized.");
            }
            if (violationProcessor == null) {
                throw new IllegalStateException("ParkingViolationProcessor could not be initialized.");
            }
            if (housingProcessor == null) {
                throw new IllegalStateException("HousingProcessor could not be initialized.");
            }

            // Start menu loop
            start(violationProcessor, populationProcessor, housingProcessor);

        } catch (ParseException e) {
            System.out.println("Error parsing JSON file: " + e.getMessage());
            return;
        } catch (IOException e) {
            System.out.println("Error reading data files: " + e.getMessage());
            return;
        } catch (Exception e) {
            System.out.println("Error reading data files: " + e.getMessage());
            return;
        }
    }

    private static boolean canReadFile(String filename) {
        File f = new File(filename);
        return f.exists() && f.isFile() && f.canRead();
    }

    private static void start(ParkingViolationProcessor violationProcessor,
                              PopulationProcessor populationProcessor,
                              HousingProcessor housingProcessor) {
        if (violationProcessor == null) {
            throw new IllegalStateException("ParkingViolationProcessor must not be null.");
        }
        if (populationProcessor == null) {
            throw new IllegalStateException("PopulationProcessor must not be null.");
        }
        if (housingProcessor == null) {
            throw new IllegalStateException("HousingProcessor must not be null.");
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            print();
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("Goodbye!");
                scanner.close();
                return; // Exit the method cleanly
            }

            // DESIGN PATTERN: Strategy - Execute strategy based on menu selection
            MenuStrategy strategy = menuStrategies.get(input);
            if (strategy != null) {
                strategy.execute(violationProcessor, populationProcessor, housingProcessor, scanner);
            } else {
                System.out.println("Invalid selection. Please try again.");
            }
            
            // Blank line after each operation
            System.out.println();
        }
    }

    // Strategy implementations for each menu option

    // Menu option 1: Display total populations for all ZIP codes.
    private static void handleTotalPopulation(ParkingViolationProcessor violationProcessor,
                                             PopulationProcessor populationProcessor,
                                             HousingProcessor housingProcessor,
                                             Scanner scanner) {
        if (populationProcessor == null) {
            System.out.println("Error: PopulationProcessor is not available.");
            return;
        }
        try {
            int totalPop = populationProcessor.totalPopulation();
            System.out.println("Total population: " + totalPop);
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred while computing total population: " + e.getMessage());
        }
    }

    // Menu option 2: Display fines per capita for each ZIP Code.
    private static void handleFinesPerCapita(ParkingViolationProcessor violationProcessor,
                                             PopulationProcessor populationProcessor,
                                             HousingProcessor housingProcessor,
                                             Scanner scanner) {
        if (violationProcessor == null) {
            System.out.println("Error: ParkingViolationProcessor is not available.");
            return;
        }
        try {
            Map<Integer, Double> finesPerCapita = violationProcessor.calculateFinesPerCapita();
            if (finesPerCapita == null) {
                System.out.println("Error: Could not calculate fines per capita.");
                return;
            }

            // Display results
            System.out.printf("%-12s %-15s%n", "Zip Code:", "Fines per Capita:");
            for (Map.Entry<Integer, Double> entry : finesPerCapita.entrySet()) {
                if (entry == null || entry.getKey() == null || entry.getValue() == null) {
                    continue;
                }
                Integer zip_code = entry.getKey();
                Double perCapita = entry.getValue();

                // Format: 4 decimal places with trailing zeros
                System.out.printf("%-12d %-15.4f%n", zip_code, perCapita);
            }
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred while calculating fines per capita: " + e.getMessage());
        }
    }

    // Menu option 3: Display Average residential market value for a ZIP Code.
    private static void handleAverageResidentialMarket(ParkingViolationProcessor violationProcessor,
                                                       PopulationProcessor populationProcessor,
                                                       HousingProcessor housingProcessor,
                                                       Scanner scanner) {
        if (housingProcessor == null) {
            System.out.println("Error: HousingProcessor is not available.");
            return;
        }
        System.out.print("Enter ZIP code: ");
        String input = scanner.nextLine().trim();

        if (!input.matches("\\d{5}")) {
            System.out.println("Invalid ZIP code. Please enter a 5-digit ZIP code.");
            return;
        }

        int zipCode = Integer.parseInt(input);

        try {
            int avg = housingProcessor.getAverageMarketValue(zipCode);

            if (avg <= 0) {
                System.out.println("No valid residential market value data found for ZIP code " + zipCode + ".");
            } else {
                System.out.println("Average residential market value for " + zipCode + ": $" + avg);
            }
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ZIP code. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred while calculating the average market value: " + e.getMessage());
        }
    }

    // Menu option 4: Average residential total livable area for a ZIP Code.
    private static void handleLivableArea(ParkingViolationProcessor violationProcessor,
                                         PopulationProcessor populationProcessor,
                                         HousingProcessor housingProcessor,
                                         Scanner scanner) {
        if (housingProcessor == null) {
            System.out.println("Error: HousingProcessor is not available.");
            return;
        }
        System.out.print("Enter ZIP code: ");
        String input = scanner.nextLine().trim();

        if (!input.matches("\\d{5}")) {
            System.out.println("Invalid ZIP code. Please enter a 5-digit ZIP code.");
            return;
        }

        int zipCode = Integer.parseInt(input);

        try {
            int avg = housingProcessor.getAverageLivableArea(zipCode);

            if (avg <= 0) {
                System.out.println("No valid livable area data found for ZIP code " + zipCode + ".");
            } else {
                System.out.println("Average residential total livable area for " + zipCode + ":\n" + avg + " square feet");
            }

        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ZIP code. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred while calculating the average livable area: " + e.getMessage());
        }
    }

    // Menu option 5: Display residential market value per capita for a ZIP Code.
    private static void handlePerCapitalResidentialValue(ParkingViolationProcessor violationProcessor,
                                                         PopulationProcessor populationProcessor,
                                                         HousingProcessor housingProcessor,
                                                         Scanner scanner) {
        if (housingProcessor == null) {
            System.out.println("Error: HousingProcessor is not available.");
            return;
        }
        System.out.print("Enter ZIP code: ");
        String input = scanner.nextLine().trim();

        if (!input.matches("\\d{5}")) {
            System.out.println("Invalid ZIP code. Please enter a 5-digit ZIP code.");
            return;
        }
        int zipCode = Integer.parseInt(input);

        try {
            int result = housingProcessor.getMarketValuePerCapita(zipCode);

            if (result <= 0) {
                System.out.println("No valid market value or population data available for ZIP code " + zipCode + ".");
            } else {
                System.out.println("Residential market value per capita for " + zipCode + ": \n$" + result);
            }

        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ZIP code. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred while calculating market value per capita: " + e.getMessage());
        }
    }

    // Menu option 6: Display property value summary for a ZIP Code.
    private static void handlePropertyValueSummary(ParkingViolationProcessor violationProcessor,
                                                   PopulationProcessor populationProcessor,
                                                   HousingProcessor housingProcessor,
                                                   Scanner scanner) {
        if (housingProcessor == null) {
            System.out.println("Error: HousingProcessor is not available.");
            return;
        }
        System.out.print("Enter ZIP code: ");
        String input = scanner.nextLine().trim();

        if (!input.matches("\\d{5}")) {
            System.out.println("Invalid ZIP code. Please enter a 5-digit ZIP code.");
            return;  // Stop before continuing
        }

        int zipCode = Integer.parseInt(input);

        try {
            HousingProcessor.PropertyValueSummary summary = housingProcessor.getPropertyValueSummary(zipCode);
            if (summary == null) {
                System.out.println("Error: Could not retrieve property value summary.");
                return;
            }

            if (summary.getMin() == 0 && summary.getMax() == 0 && summary.getMedian() == 0) {
                System.out.println("No valid residential market value data found for ZIP code " + zipCode + ".");
            } else {
                System.out.println("Property value data summary for " + zipCode + "($)"
                                    + ": \n    Min: " + summary.getMin()
                                    + "\n    Max: " + summary.getMax()
                                    + "\n    Median: " + summary.getMedian());
            }

        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ZIP code. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred while retrieving property value summary: " + e.getMessage());
        }
    }

    // Menu option 7: Display most common violation type for a ZIP code, shows top 3 violation types with counts and percentage
    private static void handleMostCommonViolation(ParkingViolationProcessor violationProcessor,
                                                  PopulationProcessor populationProcessor,
                                                  HousingProcessor housingProcessor,
                                                  Scanner scanner) {
        if (violationProcessor == null) {
            System.out.println("Error: ParkingViolationProcessor is not available.");
            return;
        }
        System.out.print("Enter ZIP code: ");

        try {
            int zipCode = Integer.parseInt(scanner.nextLine().trim());

            // Get all violation types for this ZIP
            Map<String, Integer> types = violationProcessor.getViolationTypesForZip(zipCode);
            if (types == null) {
                System.out.println("Error: Could not retrieve violation types.");
                return;
            }

            if (types.isEmpty()) {
                System.out.println("No violations found for ZIP code " + zipCode);
            } else {
                // Calculate total violations
                int totalViolations = 0;
                for (Integer count : types.values()) {
                    if (count != null) {
                        totalViolations += count;
                    }
                }

                // Sort violations by count (descending)
                List<Map.Entry<String, Integer>> sortedViolations = new ArrayList<>(types.entrySet());
                sortedViolations.sort((a, b) -> {
                    if (a == null || b == null || a.getValue() == null || b.getValue() == null) {
                        return 0;
                    }
                    return b.getValue().compareTo(a.getValue());
                });

                // Display results
                System.out.println("\n=== Violation Summary for ZIP " + zipCode + " ===");
                System.out.println("Total violations: " + totalViolations);
                System.out.println("\nTop violation types:");

                // Show top 3 (or fewer if less than 3 types exist)
                int displayCount = Math.min(3, sortedViolations.size());
                for (int i = 0; i < displayCount; i++) {
                    Map.Entry<String, Integer> entry = sortedViolations.get(i);
                    if (entry == null || entry.getKey() == null || entry.getValue() == null) {
                        continue;
                    }
                    String type = entry.getKey();
                    int count = entry.getValue();
                    double percentage = (double) count / totalViolations * 100;

                    System.out.printf("%d. %s: %d (%.2f%%)\n",
                            i + 1, type, count, percentage);
                }
            }
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ZIP code. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred while retrieving violation data: " + e.getMessage());
        }
    }

    private static void print() {
        System.out.println("==== Main Menu ====");
        System.out.println("0. Exit");
        System.out.println("1. Total population for all ZIP Codes");
        System.out.println("2. Fines per capita for each ZIP Code");
        System.out.println("3. Average residential market value for a ZIP Code");
        System.out.println("4. Average residential total livable area for a ZIP Code");
        System.out.println("5. Residential market value per capita for a ZIP Code");
        System.out.println("6. Property value summary for a ZIP Code");
        System.out.println("7. Most common violation type for a ZIP Code");
        System.out.print("Enter selection: ");
    }

}
