package coviddatasystem.datamanagement;

import coviddatasystem.util.ZipCode;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public interface CovidReader {
    
    // regex representing valid timestamp format for a covid record (YYYY-MM-DD hh:mm:ss)
    public static final Pattern VALID_TIMESTAMP = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");

    /**
     * Records valid zip codes parsed from covid reader
     * @param records list containing all valid records as String arrays
     * @return map associating numerical zip code value with a ZipCode object storing data
     */
    public Map<Integer, ZipCode> covidParser(List<String[]> records);
}
