package coviddatasystem.datamanagement;

import coviddatasystem.util.ZipCode;

import java.io.IOException;
import java.util.*;

public class PopulationCsvReader extends CsvReader {
    protected Map<Integer, Integer> populationMap = new HashMap<>();


    // Find the indices of "zip_code" and "population" columns
    int zipCodeIndex;
    int populationIndex;

    /**
     * Instantiates char reader based on input CSV file
     *
     * @param filename String representing name of input CSv file
     * @throws IOException when the underlying reader encountered an error
     */
    public PopulationCsvReader(String filename) throws IOException {
        super(filename);
    }

    /**
     * Reads entire population CSV file, recording field/column titles and storing all valid records in a list
     * @throws IOException when the underlying reader encountered an error
     * @throws CSVFormatException when the CSV file is formatted incorrectly
     */
    public void readPopulationCSV() throws IOException, CSVFormatException{
        // read in header row by calling readRow() to determine column of zipCode and population
        this.fieldTitles = Arrays.asList(readRow());
        zipCodeIndex = fieldTitles.indexOf("zip_code");
        populationIndex = fieldTitles.indexOf("population");


        String[] record;

        //reading each row and check if zipcode is 5 digits and population is integer, add both fields as key pair in map
        while ((record = this.readRow()) != null) {
            String zipCode = record[zipCodeIndex];
            String population = record[populationIndex];
            
            if (record[zipCodeIndex].length() != 5 || !isDouble(population)) {
                // if zip code isn't 5 digits, record is invalid and skip loop iteration
                continue;
            }
            
            this.populationMap.put(Integer.parseInt(zipCode), Integer.parseInt(population));
        }
    }

    /**
     * Updates input zipCode map with population data
     * @param zipCodes zipCodes map mapping zip code values to existing zipCode objects parsed from covid file                     
     */
    public Map<Integer, ZipCode> populationParser(Map<Integer, ZipCode> zipCodes) {

        // iterate through population map, update zipCode object's population or create new zipCode object
        for (Map.Entry<Integer, Integer> entry: populationMap.entrySet()) {
            int zip = entry.getKey();
            int population = entry.getValue();
            
            if (zipCodes.containsKey(zip)) {
                ZipCode zipCodeObj = zipCodes.get(zip);
                zipCodeObj.setPopulation(population);
            }
            else {
                zipCodes.put(zip, new ZipCode(zip, population));
            }
        }
        return zipCodes;
    }
    
}
