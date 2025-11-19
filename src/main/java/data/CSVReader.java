package data;

import java.io.IOException;
import java.util.List;

public interface CSVReader<T> {
    List<T> readData() throws IOException;
}
