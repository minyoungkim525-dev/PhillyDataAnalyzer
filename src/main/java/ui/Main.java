package ui;

import common.ParkingViolation;
import common.House;
import data.*;
import processor.ParkingViolationProcessor;

import java.io.File;
import java.io.IOException;
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

            // Create processor
            ParkingViolationProcessor violationProcessor = new ParkingViolationProcessor(violations, populations);

            // Start menu loop
            start(violationProcessor);

        } catch (Exception e) {
            System.out.println("Error reading data files: " + e.getMessage());
            return;
        }
    }

    private static boolean canReadFile(String filename) {
        File f = new File(filename);
        return f.exists() && f.isFile() && f.canRead();
    }

    private static void start(ParkingViolationProcessor violationProcessor) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            print();
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("Goodbye!");
                break;
            } else if (input.equals("2")) {
                handleFinesPerCapita(violationProcessor);
            } else {
                // Handle other menu options later
                System.out.println("Feature not yet implemented.");
            }
        }

        scanner.close();
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

    private static void print() {
        System.out.println("==== Main Menu ====");
        System.out.println("0. Exit");
        System.out.println("1. Total population for all ZIP Codes");
        System.out.println("2. Fines per capita for each ZIP Code");
        System.out.println("3. Average residential market value for a ZIP Code");
        System.out.println("4. Average residential total livable area for a ZIP Code");
        System.out.println("5. Residential market value per capita for a ZIP Code");
        System.out.print("Enter selection: ");
    }

}
