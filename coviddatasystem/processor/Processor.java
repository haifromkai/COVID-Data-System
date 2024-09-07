package coviddatasystem.processor;

import java.util.*;

import coviddatasystem.logging.Logger;
import coviddatasystem.util.ZipCode;

public class Processor {
    private Map<Integer, ZipCode> zipCodes;

    private Logger logger;
    
    // record calculated results in instance vars to avoid repeated calculations (memoization)
    private Integer totalPopulation;
    private TreeMap<Integer, Double> partialVacsPerCapita = new TreeMap<>();
    private TreeMap<Integer, Double> fullVacsPerCapita = new TreeMap<>();

    private Map<Integer, Integer> averageMarketValueMap = new HashMap<>();
    private Map<Integer, Integer> averageLivableAreaMap = new HashMap<>();
    private Map<Integer, Integer> marketValuePerCapitaMap = new HashMap<>();
    private Map<Integer, Integer> livableAreaPerCapitaMap = new HashMap<>();
    

    /**
     * Constructs Processor object whose function is to analyze input zip code data returned from readers
     * @param zipCodes map mapping zipCode values to ZipCode objects storing data from all 3 input files
     * @param logger Singleton instance of logger
     */
    public Processor(Map<Integer, ZipCode> zipCodes, Logger logger) {
        this.zipCodes = zipCodes;
        this.logger = logger;
    }
    
    //action 2 - calculate total population of all valid zip codes
    public Integer getTotalPopulation() {
        // if totalPopulation hasn't been calculated (this.totalPopulation == null), perform calculation and update
        // instance var
        if (this.totalPopulation == null) {
            Integer popSum = 0;
   
            // iterate over map to update sum
            for (Map.Entry<Integer, ZipCode> entry : zipCodes.entrySet()) {
                // retrieve ZipCode object associated with each zip code and its population, then update popSum
                popSum += entry.getValue().getPopulation();
            }

            this.totalPopulation = popSum;
        }
        // return value of instance var
        return this.totalPopulation;
    }
    
    //action 3
    /**
     * Retrieves either partial or total vaccinations for each valid zip code
     * @param date String representing timestamp
     * @param partialOrTotal boolean is true if wanting to show partial vacs, false if wanting to show total vacs
     * @return treeMap mapping zip codes to partial or total vaccinations
     */
    public TreeMap<Integer, Double> getVaccinationsPerCapita(String date, Boolean partialOrTotal) {
        
        if ((!partialOrTotal) && !partialVacsPerCapita.isEmpty()) {
            // if partial vacs has already been determined before, return map (memoization)
            return this.partialVacsPerCapita;
        }
        
        if ((!partialOrTotal) && !fullVacsPerCapita.isEmpty()) {
            // if partial vacs has already been determined before, return map (memoization)
            return this.fullVacsPerCapita;
        }
        
        
        // iterate through map of valid zip codes
        for (Map.Entry<Integer, ZipCode> entry : zipCodes.entrySet()) {
            // retrieve zipCode object
            ZipCode zipCodeObj = entry.getValue();
            
            // retrieve map mapping timestamps to covid data for zip code
            Map<String, Double[]> covidDataAtTime = zipCodeObj.getCovidDataAsOfTime();
            
            // if total pop for zip code is 0 or unknown, ignore it and skip to next iteration
            if (zipCodeObj.getPopulation() == 0) {
                continue;
            }
            
            // store matching timestamp entry
            Map.Entry<String, Double[]> matchingEntry = null;
            
            // iterate through timestamp records for the zip code
            for (Map.Entry<String, Double[]> timeStampEntry : covidDataAtTime.entrySet()) {
                
                // retrieve only date portion of entry's timestamp (YYYY-MM-DD)
                String entryTimeStamp = timeStampEntry.getKey().substring(0, 10);
                
                if (entryTimeStamp.equals(date)) {
                    // if entry's date matches input date, record as matchingEntry
                    matchingEntry = timeStampEntry;
                    break;
                }
            }
            
            if (partialOrTotal) {
                // if matching entry not found after iteration, total partial vacs should be set to 0
                if (matchingEntry == null) {
                    this.partialVacsPerCapita.put(zipCodeObj.getZipCode(), 0.0000);
                    // continue to next iteration of zip code for loop
                    continue;
                }
                // otherwise, calculate partial vacs per capita
                Double partialVacsPerCapita = matchingEntry.getValue()[0] / zipCodeObj.getPopulation();
                this.partialVacsPerCapita.put(zipCodeObj.getZipCode(), partialVacsPerCapita);
            } else {
                // if matching entry is not updated after iteration, total full vacs should be set to 0
                if (matchingEntry == null) {
                    this.fullVacsPerCapita.put(zipCodeObj.getZipCode(), 0.0000);
                    // continue to next iteration of zip code for loop
                    continue;
                }
                // otherwise, calculate full vacs per capita
                Double totalVacsPerCapita = matchingEntry.getValue()[1] / zipCodeObj.getPopulation();
                this.fullVacsPerCapita.put(zipCodeObj.getZipCode(), totalVacsPerCapita);
            }
        }
       
        // after checking all zip codes, return updated vacs per capita values
        if (partialOrTotal) {
            return this.partialVacsPerCapita;
        } else {
            return this.fullVacsPerCapita;
        }
    }

    //helper method to calculate averages for action 4 and 5 (implements Strategy design pattern)
    public Integer calculateAverageMethod (ArrayList<Double> doublesList) {
        if (doublesList.isEmpty()) {
            return 0;
        }
        int count = 0;
        double total = 0;
        for (Double value: doublesList) {
            count++;
            total += value;
        }
        return (int) total/count;
    }

    //helper method to retrieve doubles list for calculating average
    public ArrayList<Double> getDoublesList(String inputZip, Integer actionNumber) {
            // parse input zipCode to integer
            Integer inputZipNum = Integer.parseInt(inputZip);
            
            // retrieve ZipCode object from zipCodes map
            ZipCode zipCode = zipCodes.get(inputZipNum);
            
            if (zipCode != null) {
                // if zipCode exists in map, retrieve specified list
                if (actionNumber == 4 || actionNumber == 6) {
                    return zipCode.getPropertiesMarketValueList();
                }
                if (actionNumber == 5 || actionNumber == 7) {
                    return zipCode.getPropertiesLivableAreaList();
                }

            }
        // otherwise, return empty list     
        return new ArrayList<>();
    }

    //action 6 and 7
    public Integer calculateTotalValuePerCapita(ArrayList<Double> doublesList, Integer population) {

        //check if list is empty or population is 0, return 0
        if (doublesList.isEmpty() || population == 0) {
            return 0;
        }

        //loop over doubles list and calculate total value per capita
        double totalValue = 0;
        for (Double value: doublesList) {
            totalValue += value;
        }
        return (int) (totalValue/population);
    }
    
    //retrieve population from given inputZip
    public Integer getZipCodePopulation(String inputZip) {
        // parse inputZip to integer
        Integer inputZipNum = Integer.parseInt(inputZip);
        
        if (zipCodes.containsKey(inputZipNum)) {
            return zipCodes.get(inputZipNum).getPopulation();
        } else {
            return 0;
        }
    }

    /**
     * Retrieve Map of zipCode and its ZipCode Object
     * @return
     */
    public Map<Integer, ZipCode> getZipCodes() {
        return zipCodes;
    }

    //helper method for getting average value
    public Integer getAverage(String inputZip, Map<Integer, Integer> averageMap, Integer actionNumber) {

        //parse input string to integer, check if map already contains input zip as key, retrieve value
        int zip = Integer.parseInt(inputZip);
        if (averageMap.containsKey(zip)) {
            return averageMap.get(zip);
        } else {
            //otherwise, calculate average and put key: zip value: average pair in map
            ArrayList<Double> list = this.getDoublesList(inputZip, actionNumber);
            int result = this.calculateAverageMethod(list);
            averageMap.put(zip, result);
            return result;
        }
    }

    //action 4 get AverageMarketValue method
    public Integer getAverageMarketValue(String inputZip) {
        return getAverage(inputZip, averageMarketValueMap, 4);
    }

    //action 5 get AverageLivableArea method
    public Integer getAverageLivableArea(String inputZip) {
        return getAverage(inputZip, averageLivableAreaMap, 5);
    }

    //helper method for getting total value per capita
    public Integer getTotalValuePerCapita(String inputZip, Map<Integer, Integer> valuePerCapitaMap, Integer actionNumber) {

        //parse input string to integer, check if map already contains input zip as key, retrieve value
        int zip = Integer.parseInt(inputZip);
        if (valuePerCapitaMap.containsKey(zip)) {
            return valuePerCapitaMap.get(zip);
        }

        // otherwise, calculate total value per capita and put key: zip value: average pair in map
        else {
            ArrayList<Double> list = this.getDoublesList(inputZip, actionNumber);
            Integer population = this.getZipCodePopulation(inputZip);
            int result = this.calculateTotalValuePerCapita(list, population);
            valuePerCapitaMap.put(zip, result);
            return result;
        }
    }

    //action 6 get MarketValuePerCapita method
    public Integer getMarketValuePerCapita(String inputZip) {
        return getTotalValuePerCapita(inputZip, marketValuePerCapitaMap, 6);
    }

    // action 7 get UnvaccinatedPersonsPerTotalLiveableArea (custom feature)

    /**
     * Calculates percentage of Unvaccinated People in a Liveable Area for each ZIP Code
     * Population - (Fully Vaccinated + Partially Vaccinated) / Total Liveable Area of a ZIP Code * 100
     * @return map mapping zip code values to doubles representing # of unvaccinated persons per 100 sq ft
     */
    public Map<Integer, Double> getUnvaccinatedPersonsPerTotalLiveableArea() {
        // initialize map to return
        Map<Integer, Double> resultMap = new TreeMap<>();

        for (Map.Entry<Integer, ZipCode> entry : zipCodes.entrySet()) {
            ZipCode zipCode = entry.getValue();
            
            TreeMap<String, Double[]> covidData = zipCode.getCovidDataAsOfTime();

            // get most recent vaccination data from last entry in covidData
            Map.Entry<String, Double[]> mostRecentEntry = covidData.lastEntry();
            
            // retrieve total population of zip code
            Integer population = zipCode.getPopulation();
            
            if (mostRecentEntry == null) {
                // if no covid data associated with a timestamp, continue to next zip code
                continue;
            }
            
            Double[] entryCovidData = mostRecentEntry.getValue();

            Double partialVacs = entryCovidData[0];
            Double fullVacs = entryCovidData[1];

            // determine total number of people who are vaccinated
            Double totalVacs = partialVacs + fullVacs;

            // retrieve list of property livable areas in zip code
            ArrayList<Double> livableAreas = zipCode.getPropertiesLivableAreaList();

            // initialize double to record total livable area
            double totalLivableArea = 0.0;

            for (Double livableArea : livableAreas) {
                totalLivableArea += livableArea;
            }
            
            if (totalLivableArea <= 0.0) {
                // if total livable area is less than or equal to 0, continue to next zip code
                continue;
            }
            
            // perform calculations
            Double result = ((population - totalVacs) / totalLivableArea) * 100;
            
            // ignore negative results (from wonky populations)
            if (result <= 0.0) {
                continue;
            }
            
            // put entry into resultMap
            resultMap.put(entry.getKey(), result);
        }
        
        return resultMap;
    }
    
}
