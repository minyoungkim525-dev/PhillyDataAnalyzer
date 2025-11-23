package data;

import common.House;
import common.PropertyColumn;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HousingReader implements CSVReader<House> {
    private final String filename;

    public HousingReader(String filename) {
        this.filename = filename;
    }

    @Override
    public List<House> readData() throws IOException {
        List<House> houses = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String headerLine = br.readLine();

            if (headerLine == null) {
                throw new IOException("CSV file is empty");
            }

            Map<PropertyColumn, Integer> columnIndices = parseHeader(headerLine);

            validateRequiredColumns(columnIndices);

            int marketValueIndex = columnIndices.get(PropertyColumn.MARKET_VALUE);
            int totalLivableAreaIndex = columnIndices.get(PropertyColumn.TOTAL_LIVABLE_AREA);
            int zipCodeIndex = columnIndices.get(PropertyColumn.ZIP_CODE);

            String line;
            while ((line = br.readLine()) != null) {
                House house = parseLine(line, marketValueIndex, totalLivableAreaIndex, zipCodeIndex);
                if (house != null) {
                    houses.add(house);
                }
            }
        }
        return houses;
    }

    private Map<PropertyColumn, Integer> parseHeader(String headerLine) {
        Map<PropertyColumn, Integer> columnIndices = new HashMap<>();
        String[] headers = headerLine.split(",");

        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].trim().toLowerCase();

            for (PropertyColumn column: PropertyColumn.values()) {
                if (column.getColumnName().equals(header)) {
                    columnIndices.put(column, i);
                    break;
                }
            }
        }

        return columnIndices;
    }

    private void validateRequiredColumns(Map<PropertyColumn, Integer> columnIndices) throws IOException {
        for (PropertyColumn column: PropertyColumn.values()) {
            if (!columnIndices.containsKey(column)) {
                throw new IOException("CSV file is missing required column");
            }
        }
    }

    private House parseLine(String line, int marketValueIndex, int totalLivableAreaIndex, int zipCodeIndex) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] fields = line.split(",", -1);
        int maxIndex = Math.max(Math.max(marketValueIndex, totalLivableAreaIndex), zipCodeIndex);
        if (fields.length <= maxIndex) {
            return null;
        }

        Integer zip_code = extractZipCode(fields[zipCodeIndex].trim());
        if (zip_code == null) {
            return null;
        }

        Integer market_value = parsePositiveInteger(fields[marketValueIndex].trim());
        Integer total_livable_area = parsePositiveInteger(fields[totalLivableAreaIndex].trim());

        return new House(zip_code, market_value, total_livable_area);
    }

    private Integer extractZipCode(String zipCodeField) {
        if (zipCodeField == null || zipCodeField.isEmpty()) {
            return null;
        }

        String digitsOnly = zipCodeField.replaceAll("[^0-9]", "");

        if (digitsOnly.length() >= 5) {
            try {
                String fiveDigits = digitsOnly.substring(0, 5);
                return Integer.parseInt(fiveDigits);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    private Integer parsePositiveInteger(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            int parsed = Integer.parseInt(value);
            return (parsed > 0) ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
