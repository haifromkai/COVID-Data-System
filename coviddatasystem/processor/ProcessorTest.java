package coviddatasystem.processor;

import java.io.IOException;
import java.util.*;

import coviddatasystem.datamanagement.*;
import coviddatasystem.logging.Logger;
import coviddatasystem.util.ZipCode;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

public class ProcessorTest {
    private static CovidCsvReader csvReader;
    private static PopulationCsvReader popReader;
    private static PropertiesCsvReader propReader;
    private static Map<Integer, ZipCode> zipCodes;
    private static Processor processor;
    private static Logger logger = Logger.getInstance();
    
    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        // parse covid data from csv
        csvReader = new CovidCsvReader("covid_data.csv");
        csvReader.readCovidCSV();
        ArrayList<String[]> records = csvReader.getRecords();
        zipCodes = csvReader.covidParser(records);
        
        // instantiate population and properties readers, and read data
        popReader = new PopulationCsvReader("population.csv");
        propReader = new PropertiesCsvReader("properties.csv");
        
        popReader.readPopulationCSV();
        propReader.readPropertiesCSV();
        
        // pass zipCodes map to population and properties parsers
        popReader.populationParser(zipCodes);
        propReader.propertiesParser(zipCodes);
        
        // instantiate processor with updated zipCode map and logger singleton instance
        processor = new Processor(zipCodes, logger);
    }
    
    @Test
    void testTotalPopulation() {
        // check that total population of all zip codes with population data equals provided value in hw instructions
        assertEquals(1603797, processor.getTotalPopulation());
    }
    
    @Test
    void testTotalPartialVacsPerCapita() {
        // test timestamp out of range for partial vacs
        TreeMap<Integer, Double> partialVacs = processor.getVaccinationsPerCapita("2021-05-28", true);
        
        // print code for troubleshooting
        /*for (Map.Entry<Integer, Double> entry : partialVacs.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }*/
        
        // 19100 should not be in partialVacs map b/c it was not in population file
        assertNull(partialVacs.get(19100));
        
        // 19108 should have 0 partial vacs b/c it's population exists, but total partial vacs as of 2021-05-28 is unknown
        assertEquals(0.0000, partialVacs.get(19108));
    }
    
    @Test
    void testTotalFullVacsPerCapita() {
        
    }
    
    @Test
    void testUnvaccinatedPersonsPerTotalLiveableArea() {
        Map<Integer, Double> results = processor.getUnvaccinatedPersonsPerTotalLiveableArea();
        
        for (Map.Entry<Integer, Double> entry : results.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
