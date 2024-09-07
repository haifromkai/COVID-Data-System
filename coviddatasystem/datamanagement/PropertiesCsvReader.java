package coviddatasystem.datamanagement;

import coviddatasystem.util.ZipCode;

import java.io.IOException;
import java.util.*;

public class PropertiesCsvReader extends CsvReader {

    // Find the indices of "zip_code",  "total_livable_area" and "market_value" columns
    int zipCodeIndex;
    int totalLivableAreaIndex;
    int marketValueIndex;

    /**
     * Instantiates char reader based on input CSV file
     *
     * @param filename String representing name of input CSv file
     * @throws IOException when the underlying reader encountered an error
     */
    public PropertiesCsvReader(String filename) throws IOException {
        super(filename);
    }

    /**
     * Reads entire properties CSV file, recording field/column titles and storing all valid records in a list
     * @throws IOException when the underlying reader encountered an error
     * @throws CSVFormatException when the CSV file is formatted incorrectly
     */
    public void readPropertiesCSV() throws IOException, CSVFormatException{
        // read in header row by calling readRow() to determine column of zipCode, total livable area and market value
        this.fieldTitles = Arrays.asList(readRow());

        zipCodeIndex = fieldTitles.indexOf("zip_code");
        totalLivableAreaIndex = fieldTitles.indexOf("total_livable_area");
        marketValueIndex = fieldTitles.indexOf("market_value");

        String[] record;

        //iterate over each line and check if first 5 characters of zipcode valid, retrieve only the 3 required fields
        //zipcode, livable area and market value for each record line, add shrink record to records list
        while ((record = this.readRow()) != null) {

            String zipCode = record[zipCodeIndex];
            String totalLivableArea = record[totalLivableAreaIndex];
            String marketValue = record[marketValueIndex];

            String[] shrinkRecord = new String[3];

            if (zipCode.length() < 5 || !isInteger(zipCode.substring(0,5))) {
                continue;
            }
            
            shrinkRecord[0] = zipCode.substring(0,5);
            shrinkRecord[1] = totalLivableArea;
            shrinkRecord[2] = marketValue;

            this.records.add(shrinkRecord);
        }
    }

    /**
     * Updates input zipCode map to add properties data
     * @param zipCodes map containing existing zipCode objects parsed from covid and population files
     */
    public Map<Integer, ZipCode> propertiesParser(Map<Integer, ZipCode> zipCodes) {
        
        ZipCode zipCodeObj;
        
        // Iterate over records and update the corresponding ZipCode objects or create new zipCode objects
        for (String[] record : records) {
            int zipCode = Integer.parseInt(record[0]);
            
            if (zipCodes.containsKey(zipCode)) {
                zipCodeObj = zipCodes.get(zipCode);
            }
            else {
                //create two lists as constructor and add marketValue and livableArea to these 2 lists and create new zipCode object
                ArrayList<Double> propertiesMarketValueList = new ArrayList<>();
                ArrayList<Double> propertiesTotalLivableAreaList = new ArrayList<>();

                // create new ZipCode object
                zipCodeObj = new ZipCode(zipCode, propertiesMarketValueList, propertiesTotalLivableAreaList);
                // add entry with new ZipCode object to the zipCodes map
                zipCodes.put(zipCode, zipCodeObj);
            }

            try {
                // Update the properties of the ZipCode object
                double livableArea = Double.parseDouble(record[1]);
                zipCodeObj.getPropertiesLivableAreaList().add(livableArea);
            }
            catch (NumberFormatException e) {
                // catches exception when record[1] can't be parsed to double
            }

            try {
                double marketValue = Double.parseDouble(record[2]);
                zipCodeObj.getPropertiesMarketValueList().add(marketValue);
            }
            catch (NumberFormatException e) {
                // catches exception when record[2] can't be parsed to double
            }
        }
        return zipCodes;
    }
}
