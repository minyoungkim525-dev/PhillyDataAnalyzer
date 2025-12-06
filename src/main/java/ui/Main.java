package ui;

import common.ParkingViolation;
import common.House;
import data.*;
import processor.HousingProcessor;
import processor.ParkingViolationProcessor;
import processor.PopulationProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

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

            // Read properties
            HousingReader housingReader = new HousingReader(propertiesFile);
            List<House> houses = housingReader.readData();

            // Read population
            PopulationFileReader popReader = new PopulationFileReader(populationFile);
            Map<Integer, Integer> populations = popReader.readData();

            // Create processors
            PopulationProcessor populationProcessor = new PopulationProcessor(populations);
            ParkingViolationProcessor violationProcessor = new ParkingViolationProcessor(violations, populations);
            HousingProcessor housingProcessor = HousingProcessor.getInstance(housingReader, popReader);

            // Start menu loop
            start(violationProcessor, populationProcessor, housingProcessor);

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

        Scanner scanner = new Scanner(System.in);

        while (true) {
            print();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "0" -> {
                    System.out.println("Goodbye!");
                    scanner.close();
                    return; // Exit the method cleanly
                }
                case "1" -> handleTotalPopulation(populationProcessor);
                case "2" -> handleFinesPerCapita(violationProcessor);
                case "3" -> handleAverageResidentialMarket(housingProcessor, scanner);
                case "4" -> handleLivableArea(housingProcessor, scanner);
                case "5" -> handlePerCapitalResidentialValue(housingProcessor, scanner);
                case "6" -> handlePropertyValueSummary(housingProcessor, scanner);
                case "7" -> handleMostCommonViolation(violationProcessor, scanner);

                default -> System.out.println("Feature not yet implemented.");
            }
            // Blank line after each operation ***
            System.out.println();
        }
    }

    private static void handleFinesPerCapita(ParkingViolationProcessor processor) {
        Map<Integer, Double> finesPerCapita = processor.calculateFinesPerCapita();

        // Display results
        for (Map.Entry<Integer, Double> entry : finesPerCapita.entrySet()) {
            Integer zip_code = entry.getKey();
            Double perCapita = entry.getValue();

            // Format: 4 decimal places with trailing zeros
            System.out.printf("%d %.4f%n", zip_code, perCapita);
        }
    }

    // Menu option 2: Display total populations for all ZIP codes.
    private static void handleTotalPopulation(PopulationProcessor populationProcessor){
        try {
            int totalPop = populationProcessor.totalPopulation();
            System.out.println("Total population across all ZIP codes: " + totalPop);

        } catch (Exception e) {
            // Catch-any for unexpected processor failures
            System.out.println("An error occurred while computing total population: " + e.getMessage());
        }
    }

    // Menu option 3: Display Average residential market value for a ZIP Code.
    private static void handleAverageResidentialMarket(HousingProcessor hp, Scanner scanner) {
        System.out.print("Enter ZIP code: ");

        try {
            int zipCode = Integer.parseInt(scanner.nextLine().trim());
            int avg = hp.getAverageMarketValue(zipCode);

            if (avg <= 0) {
                System.out.println("No valid residential market value data found for ZIP code " + zipCode + ".");
            } else {
                System.out.println("Average residential market value for ZIP code " + zipCode + ": " + avg);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ZIP code. Please enter a valid number.");
        } catch (Exception e) {
            // Extra safety in case HousingProcessor throws unexpectedly
            System.out.println("An error occurred while calculating the average market value: " + e.getMessage());
        }
    }

    // Menu option 4: Average residential total livable area for a ZIP Code.
    private static void handleLivableArea(HousingProcessor hp, Scanner scanner) {
        System.out.print("Enter ZIP code: ");

        try {
            int zipCode = Integer.parseInt(scanner.nextLine().trim());
            int avg = hp.getAverageLivableArea(zipCode);

            if (avg <= 0) {
                System.out.println("No valid livable area data found for ZIP code " + zipCode + ".");
            } else {
                System.out.println("Average residential total livable area for ZIP code " + zipCode + ": " + avg);
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ZIP code. Please enter a valid number.");
        } catch (Exception e) {
            // Extra safety if the processor encounters unexpected issues
            System.out.println("An error occurred while calculating the average livable area: " + e.getMessage());
        }
    }

    // Menu option 5: Display residential market value per capita for a ZIP Code.
    private static void handlePerCapitalResidentialValue(HousingProcessor hp, Scanner scanner) {
        System.out.print("Enter ZIP code: ");

        try {
            int zipCode = Integer.parseInt(scanner.nextLine().trim());

            int result = hp.getMarketValuePerCapita(zipCode);

            if (result <= 0) {
                System.out.println("No valid market value or population data available for ZIP code " + zipCode + ".");
            } else {
                System.out.println("Residential market value per capita for ZIP code " + zipCode + ": " + result);
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ZIP code. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred while calculating market value per capita: " + e.getMessage());
        }
    }

    // Menu option 6: Display property value summary for a ZIP Code.
    private static void handlePropertyValueSummary(HousingProcessor hp, Scanner scanner) {
        System.out.print("Enter ZIP code: ");

        try {
            int zipCode = Integer.parseInt(scanner.nextLine().trim());

            HousingProcessor.PropertyValueSummary summary = hp.getPropertyValueSummary(zipCode);

            if (summary.getMin() == 0 && summary.getMax() == 0 && summary.getMedian() == 0) {
                System.out.println("No valid residential market value data found for ZIP code " + zipCode + ".");
            } else {
                System.out.println("Property value summary for ZIP code " + zipCode + ":");
                System.out.println("  Minimum Market Value: " + summary.getMin());
                System.out.println("  Maximum Market Value: " + summary.getMax());
                System.out.println("  Median Market Value : " + summary.getMedian());
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ZIP code. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred while retrieving property value summary: " + e.getMessage());
        }
    }

    // Menu option 7: Display most common violation type for a ZIP code, shows top 3 violation types with counts and percentage
    private static void handleMostCommonViolation(ParkingViolationProcessor processor, Scanner scanner) {
        System.out.print("Enter ZIP code: ");

        try {
            int zipCode = Integer.parseInt(scanner.nextLine().trim());

            // Get all violation types for this ZIP
            Map<String, Integer> types = processor.getViolationTypesForZip(zipCode);

            if (types.isEmpty()) {
                System.out.println("No violations found for ZIP code " + zipCode);
            } else {
                // Calculate total violations
                int totalViolations = 0;
                for (int count : types.values()) {
                    totalViolations += count;
                }

                // Sort violations by count (descending)
                List<Map.Entry<String, Integer>> sortedViolations = new ArrayList<>(types.entrySet());
                sortedViolations.sort((a, b) -> b.getValue().compareTo(a.getValue()));

                // Display results
                System.out.println("\n=== Violation Summary for ZIP " + zipCode + " ===");
                System.out.println("Total violations: " + totalViolations);
                System.out.println("\nTop violation types:");

                // Show top 3 (or fewer if less than 3 types exist)
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
