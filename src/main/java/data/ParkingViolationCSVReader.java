package data;

import common.ParkingViolation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParkingViolationCSVReader implements CSVReader<ParkingViolation> {

    private final String fileName;

    public ParkingViolationCSVReader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<ParkingViolation> readData() throws IOException {
        List<ParkingViolation> violations = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                // Skip completely empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] fields = line.split(",");

                String date = fields[0];
                int fine = Integer.parseInt(fields[1].trim());
                String violation = fields[2];
                String plate_id = fields[3];
                String state = fields[4];
                String ticket_number = fields[5];

                Integer zip_code = null;
                if (fields.length >= 7) {
                    String zip = fields[6].trim();
                    if (!zip.isEmpty()) {
                        try {
                            zip_code = Integer.parseInt(zip);
                        } catch (NumberFormatException e) {
                            // bad ZIP in the CSV -> treat as missing
                            zip_code = null;
                        }
                    }
                }

                ParkingViolation pv = new ParkingViolation( ticket_number, plate_id,
                                                            date, zip_code, violation,
                                                            fine,state);
                violations.add(pv);
            }
        }
        return violations;
    }
}