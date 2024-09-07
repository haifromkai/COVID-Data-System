package coviddatasystem.datamanagement;

import coviddatasystem.util.ZipCode;

import java.io.IOException;

import java.util.*;
import java.util.regex.Matcher;

public class CovidCsvReader extends CsvReader implements CovidReader {
    
    /**
     * Instantiates char reader based on input CSV file
     *
     * @param filename String representing name of input CSv file
     * @throws IOException when the underlying reader encountered an error
     */
    public CovidCsvReader(String filename) throws IOException {
        super(filename);
    }

    /**
     * Reads entire covid CSV file, recording field/column titles and storing all valid records in a list
     * @throws IOException when the underlying reader encountered an error
     * @throws CSVFormatException when the CSV file is formatted incorrectly
     */
    public void readCovidCSV() throws IOException, CSVFormatException {
        // read in header row by calling readRecord() and update fieldTitles list
        this.fieldTitles = Arrays.asList(readRow());

        // read in remaining records in CSV file 
        String[] record;

        while ((record = readRow()) != null) {
            // fetch indices in fieldTitles of "zip_code" and "etl_timestamp" headers
            int zipCodeIndex = this.fieldTitles.indexOf("zip_code");
            int timeStampIndex = this.fieldTitles.indexOf("etl_timestamp");
            
            // check if zip code is 5 digits
            String zipCode = record[zipCodeIndex];
            if (zipCode.length() != 5 || !isDouble(zipCode)) {
                // if length is not 5 or chars aren't numeric, record is invalid and skip to next record
                continue;
            }
            
            // use regex to check if timestamp is correctly formatted (YYYY-MM-DD hh:mm:ss)
            Matcher validTimestamp = VALID_TIMESTAMP.matcher(record[timeStampIndex]);

            if (!validTimestamp.matches()) {
                // if timestamp is incorrectly formatted, record is invalid and skip to next record
                continue;
            }
            
            for (int i = 0; i < record.length; i++) {
                if (record[i].isEmpty()) {
                    // if a field is empty, then replace with "0"
                    record[i] = "0";
                }
            }
            
            // add valid record to records list
            this.records.add(record);
        }
    }

    /**
     * Records valid zip codes parsed from covid reader
     * @param records list containing all valid records as String arrays
     * @return map associating numerical zip code value with a ZipCode object storing data
     */
    @Override
    public Map<Integer, ZipCode> covidParser(List<String[]> records) {
        // instantiate map to return
        Map<Integer, ZipCode> zipCodes = new TreeMap<>();

        // iterate through list of valid records
        for (String[] record : records) {
            // retrieve data from record
            String zipCodeStr = record[fieldTitles.indexOf("zip_code")];
            String negInfectionTests = record[fieldTitles.indexOf("NEG")];
            String posInfectionTests = record[fieldTitles.indexOf("POS")];
            String deaths = record[fieldTitles.indexOf("deaths")];
            String hospitalized = record[fieldTitles.indexOf("hospitalized")];
            String partialVac = record[fieldTitles.indexOf("partially_vaccinated")];
            String fullVac = record[fieldTitles.indexOf("fully_vaccinated")];
            String boosted = record[fieldTitles.indexOf("boosted")];
            String timeStamp = record[fieldTitles.indexOf("etl_timestamp")];

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
