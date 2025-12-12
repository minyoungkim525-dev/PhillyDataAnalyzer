import data.*;
import common.House;
import common.ParkingViolation;
import org.json.simple.parser.ParseException;
import processor.HousingProcessor;
import processor.ParkingViolationProcessor;
import processor.PopulationProcessor;
import ui.UI;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Validate arguments
        if(args.length != 4) {
            System.out.println("Invalid number of arguments.");
            return;
        }

        String format = args[0];
        if(!format.equals("csv") && !format.equals("json")) {
            System.out.println("Error: Format should be listed as either \"json\" or \"csv\".");
            return;
        }

        String violationsFile = args[1];
        String propertiesFile = args[2];
        String populationFile = args[3];

        // Validate files exist
        if (!canReadFile(violationsFile)) {
            System.out.println("Error: Cannot open parking violations file: " + violationsFile);
            return;
        }
        if (!canReadFile(propertiesFile)) {
            System.out.println("Error: Cannot open properties file: " + propertiesFile);
            return;
        }
        if (!canReadFile(populationFile)) {
            System.out.println("Error: Cannot open population file: " + populationFile);
            return;
        }

        // Read all data and create processors
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

            // Start UI with processors
            UI.start(violationProcessor, populationProcessor, housingProcessor);

        } catch (ParseException e) {
            System.out.println("Error parsing JSON file: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading data files: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error reading data files: " + e.getMessage());
        }
    }

    private static boolean canReadFile(String filename) {
        File f = new File(filename);
        return f.exists() && f.isFile() && f.canRead();
    }
}