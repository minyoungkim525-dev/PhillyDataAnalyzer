package ui;

import processor.HousingProcessor;
import processor.ParkingViolationProcessor;
import processor.PopulationProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * UI class for PhillyDataAnalyzer application.
 * Implements Strategy pattern for menu option handling.
 */
public class UI {

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
        strategies.put("1", UI::handleTotalPopulation);
        strategies.put("2", UI::handleFinesPerCapita);
        strategies.put("3", UI::handleAverageResidentialMarket);
        strategies.put("4", UI::handleLivableArea);
        strategies.put("5", UI::handlePerCapitalResidentialValue);
        strategies.put("6", UI::handlePropertyValueSummary);
        strategies.put("7", UI::handleMostCommonViolation);
        return strategies;
    }

    /**
     * Main entry point for the UI.
     * Called from Main.main() with already-initialized processors.
     */
    public static void start(ParkingViolationProcessor violationProcessor,
                             PopulationProcessor populationProcessor,
                             HousingProcessor housingProcessor) {
        runMenuLoop(violationProcessor, populationProcessor, housingProcessor);
    }

    private static void runMenuLoop(ParkingViolationProcessor violationProcessor,
                                    PopulationProcessor populationProcessor,
                                    HousingProcessor housingProcessor) {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("Goodbye!");
                scanner.close();
                return;
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
        try {
            int totalPop = populationProcessor.totalPopulation();
            System.out.println("Total population: " + totalPop);
        } catch (Exception e) {
            System.out.println("An error occurred while computing total population: " + e.getMessage());
        }
    }

    // Menu option 2: Display fines per capita for each ZIP Code.
    private static void handleFinesPerCapita(ParkingViolationProcessor violationProcessor,
                                             PopulationProcessor populationProcessor,
                                             HousingProcessor housingProcessor,
                                             Scanner scanner) {
        Map<Integer, Double> finesPerCapita = violationProcessor.calculateFinesPerCapita();

        // Display results
        System.out.printf("%-12s %-15s%n", "Zip Code:", "Fines per Capita:");
        for (Map.Entry<Integer, Double> entry : finesPerCapita.entrySet()) {
            Integer zip_code = entry.getKey();
            Double perCapita = entry.getValue();

            // Format: 4 decimal places with trailing zeros
            System.out.printf("%-12d %-15.4f%n", zip_code, perCapita);
        }
    }

    // Menu option 3: Display Average residential market value for a ZIP Code.
    private static void handleAverageResidentialMarket(ParkingViolationProcessor violationProcessor,
                                                       PopulationProcessor populationProcessor,
                                                       HousingProcessor housingProcessor,
                                                       Scanner scanner) {
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
        System.out.print("Enter ZIP code: ");
        String input = scanner.nextLine().trim();

        if (!input.matches("\\d{5}")) {
            System.out.println("Invalid ZIP code. Please enter a 5-digit ZIP code.");
            return;
        }

        int zipCode = Integer.parseInt(input);

        try {
            HousingProcessor.PropertyValueSummary summary = housingProcessor.getPropertyValueSummary(zipCode);

            if (summary.getMin() == 0 && summary.getMax() == 0 && summary.getMedian() == 0) {
                System.out.println("No valid residential market value data found for ZIP code " + zipCode + ".");
            } else {
                System.out.println("Property value data summary for " + zipCode + "($)"
                        + ": \n    Min: " + summary.getMin()
                        + "\n    Max: " + summary.getMax()
                        + "\n    Median: " + summary.getMedian());
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ZIP code. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred while retrieving property value summary: " + e.getMessage());
        }
    }

    // Menu option 7: Display most common violation type for a ZIP code
    private static void handleMostCommonViolation(ParkingViolationProcessor violationProcessor,
                                                  PopulationProcessor populationProcessor,
                                                  HousingProcessor housingProcessor,
                                                  Scanner scanner) {
        System.out.print("Enter ZIP code: ");

        try {
            int zipCode = Integer.parseInt(scanner.nextLine().trim());

            Map<String, Integer> types = violationProcessor.getViolationTypesForZip(zipCode);

            if (types.isEmpty()) {
                System.out.println("No violations found for ZIP code " + zipCode);
            } else {
                int totalViolations = 0;
                for (int count : types.values()) {
                    totalViolations += count;
                }

                List<Map.Entry<String, Integer>> sortedViolations = new ArrayList<>(types.entrySet());
                sortedViolations.sort((a, b) -> b.getValue().compareTo(a.getValue()));

                System.out.println("\n=== Violation Summary for ZIP " + zipCode + " ===");
                System.out.println("Total violations: " + totalViolations);
                System.out.println("\nTop violation types:");

                int displayCount = Math.min(3, sortedViolations.size());
                for (int i = 0; i < displayCount; i++) {
                    Map.Entry<String, Integer> entry = sortedViolations.get(i);
                    String type = entry.getKey();
                    int count = entry.getValue();
                    double percentage = (double) count / totalViolations * 100;

                    System.out.printf("%d. %s: %d (%.2f%%)\n",
                            i + 1, type, count, percentage);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ZIP code. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred while retrieving violation data: " + e.getMessage());
        }
    }

    private static void printMenu() {
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