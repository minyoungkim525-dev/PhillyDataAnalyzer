package data;

import java.io.IOException;
import java.util.Map;

public interface PopulationReader {

    Map<Integer, Integer> readData() throws IOException;

}
