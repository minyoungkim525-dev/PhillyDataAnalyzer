package data;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

public interface JSONReader<T> {
    public List<T> readData() throws IOException, ParseException;
}
