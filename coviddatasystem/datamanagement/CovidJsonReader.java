package coviddatasystem.datamanagement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import coviddatasystem.util.ZipCode;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

import static coviddatasystem.datamanagement.CsvReader.isDouble;

public class CovidJsonReader implements CovidReader {
    protected String fileName;
    
    protected ArrayList<String[]> records = new ArrayList<>();
    
    protected String[] fieldTitles = {"zip_code", "NEG", "POS", "deaths", "hospitalized", "partially_vaccinated", 
    "fully_vaccinated", "boosted", "etl_timestamp"};
    

    /**
     * Creates instance of CovidJsonReader class by taking in a CSV file as an argument
     * @param fileName String representing name of CSV file
     */
    public CovidJsonReader(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Determine if JSONObject represents valid record and store its fields in String array
     * @param jsonObject represents a record in JSON file
     * @return String array containing all fields if record is valid, otherwise null
     */
    private String[] readRecord(JSONObject jsonObject) {
        String zipCode = jsonObject.get("zip_code").toString();
        
        if (zipCode.length() != 5 || !isDouble(zipCode)) {
            // if zip code isn't 5 digits, record is invalid
            return null;
        }
        
        // use regex to check if timestamp is correctly formatted (YYYY-MM-DD hh:mm:ss)
        Matcher validTimestamp = VALID_TIMESTAMP.matcher((String) jsonObject.get("etl_timestamp"));
        
        if (!validTimestamp.matches()) {
            // if timestamp is incorrectly formatted, record is invalid
            return null;
        }
        
        // if record is valid, parse fields and populate string array
        String[] fields = new String[fieldTitles.length];
        
        // iterate through array of field titles and retrieve corresponding values from json object
        for (int i = 0; i < fields.length; i++) {
            if (jsonObject.containsKey(fieldTitles[i])) {
                // if json object has field title as a key, add corresponding value to fields array
                fields[i] = jsonObject.get(fieldTitles[i]).toString();
            } else {
                // else, if json object doesn't contain the field, set that field's default value to 0
                fields[i] = "0";
            }
        }
        
        return fields;
    }

    /**
     * Uses JSON Simple library and readRecord() helper method to parse given JSON file
     * @return updated list of valid records
     * @throws IOException occurs when there is error instantiating FileReader from fileName
     * @throws ParseException occurs when there is error parsing JSON file with JSON Simple library
     */
    public List<String[]> readCovidJson() throws IOException, ParseException {
        // parse JSON file and cast to JSONArray
        JSONArray jsonArray = (JSONArray) new JSONParser().parse(new FileReader(this.fileName));

        // iterate through objects/records in array
        for (Object obj : jsonArray) {
            // cast obj to a JSONObject to parse fields in record
            JSONObject jsonObject = (JSONObject) obj;
            
            // add valid (non-null) records to records list by calling readRecord()
            String[] record = readRecord(jsonObject);
            
            if (record != null) {
                records.add(record);
            }
        }
        // returns updated list of valid records
        return this.records;
    }

    /**
     * Records valid zip codes parsed from covid reader
     * @param records list containing all valid records as String arrays
     * @return map associating numerical zip code value with a ZipCode object storing data
     */
    @Override
    public Map<Integer, ZipCode> covidParser(List<String[]> records) {
        // instantiate map of ZipCode objects to return
        Map<Integer, ZipCode> zipCodes = new TreeMap<>();
        
        // iterate through list of valid records
        for (String[] record : records) {
            // retrieve data from record
            String zipCodeStr = record[0];
            String negInfectionTests = record[1];
            String posInfectionTests = record[2];
            String deaths = record[3];
            String hospitalized = record[4];
            String partialVac = record[5];
            String fullVac = record[6];
            String boosted = record[7];
            String timeStamp = record[8];

            // convert zip_code string to integer
            Integer zipCodeValue = Integer.parseInt(zipCodeStr);

            if (!zipCodes.containsKey(zipCodeValue)) {
                // if zipCodeValue is not a key in the map, associate it with new ZipCode instance and add to map 
                zipCodes.put(zipCodeValue, new ZipCode(zipCodeStr, negInfectionTests, posInfectionTests, deaths, hospitalized,
                        partialVac, fullVac, boosted, timeStamp));
            } else {
                // else, zip code already exists

                // retrieve ZipCode object associated with zipCodeValue
                ZipCode zipCode = zipCodes.get(zipCodeValue);

                // add entry to zip code object's covidDataAsOfTime map with updated data for new timestamp
                zipCode.getCovidDataAsOfTime().put(timeStamp, new Double[]{Double.parseDouble(partialVac), Double.parseDouble(fullVac),
                        Double.parseDouble(negInfectionTests), Double.parseDouble(posInfectionTests), Double.parseDouble(deaths),
                        Double.parseDouble(hospitalized), Double.parseDouble(boosted)});
            }
        }
        
        return zipCodes;
    }
}
