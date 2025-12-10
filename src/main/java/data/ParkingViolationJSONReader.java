package data;

import common.ParkingViolation;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParkingViolationJSONReader implements JSONReader<ParkingViolation> {

    private final String fileName;

    public ParkingViolationJSONReader(String fileName) { this.fileName = fileName; }

    @Override
    public List<ParkingViolation> readData() throws IOException, ParseException {
        List<ParkingViolation> violations = new ArrayList<>();

        FileReader fileReader = new FileReader(fileName);
        JSONParser parser = new JSONParser();
        JSONArray array = (JSONArray) parser.parse(fileReader); // Use JSONParser to parse the file's JSON content and cast it to a JSON Array.

        for (Object obj : array) {
            JSONObject pv = (JSONObject) obj;

            Object ticket = pv.get("ticket_number");
            String ticket_number = ticket == null ? null : ticket.toString();

            Object plate = pv.get("plate_id");
            String plate_id = plate == null ? null : plate.toString();

            String date = (String) pv.get("date");

            Object zipObj = pv.get("zip_code");
            Integer zip_code = null;
            if (zipObj != null) {
                try {
                    zip_code = Integer.parseInt(zipObj.toString());
                } catch (NumberFormatException e) {
                }
            }

            String violation = (String) pv.get("violation");

            Object fineObj = pv.get("fine");
            int fine = 0;
            if (fineObj instanceof Number) {
                fine = ((Number) fineObj).intValue();
            } else if (fineObj != null) {
                fine = Integer.parseInt(fineObj.toString());
            }

            String state = (String) pv.get("state");

            ParkingViolation parkingViolation = new ParkingViolation(ticket_number, plate_id, date, zip_code, violation, fine, state);
            violations.add(parkingViolation);
        }
        return violations;
    }
}
