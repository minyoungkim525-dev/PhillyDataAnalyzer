package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;

public class PopulationFileReader implements PopulationReader{

    private final String fileName;

    public PopulationFileReader(String fileName){
        this.fileName = fileName;
    }

    @Override
    public Map<Integer,Integer> readData() throws IOException {
        Map<Integer,Integer> populations = new HashMap<>();

        try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                if (parts.length < 2) continue;

                try {
                    int zip = Integer.parseInt(parts[0]);
                    int population = Integer.parseInt(parts[1]);
                    populations.put(zip, population);
                } catch (NumberFormatException e) {
                    // Skip malformed numeric values
                }
            }
        }

        return populations;
    }
}
