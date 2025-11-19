package ui;

import common.ParkingViolation;

import java.io.File;
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

        start();
    }

    // Utility method to check if the file exists and can be opened for reading (Error message 3)
    private static boolean canReadFile(String filename) {
        File f = new File(filename);
        return f.exists() && f.isFile() && f.canRead();
    }

    private static void start() {
        Scanner scanner = new Scanner(System.in);
        print();
        String input = scanner.nextLine().trim();

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
