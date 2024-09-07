package coviddatasystem.util;

import java.util.*;

public class ZipCode {
    
    // 5 digit zip code
    private Integer zipCode = 0;
    
    // maps timestamps to partial vacs, total vacs, neg tests, pos tests, booster doses, total hospitalize, and total deaths
        // timestamp in format (YYYY-MM-DD hh:mm:ss)
    private TreeMap<String, Double[]> covidDataAsOfTime = new TreeMap<>();
    
    private Double totalProperties = 0.0;
    
    private Integer population = 0;

    private ArrayList<Double> propertiesMarketValueList = new ArrayList<Double>();

    private ArrayList<Double> propertiesTotalLivableAreaList = new ArrayList<Double>();
    
    // constructors
    
    /**
     * Constructs ZipCode instance by passing in String COVID data and casting to appropriate data types
     */
    public ZipCode(String zipCode, String negInfectionTests, String posInfectionTests, String deaths, String hospitalized,
                   String partialVac, String fullVac, String boosted, String timeStamp) {
        this.zipCode = Integer.parseInt(zipCode);
        
        // add entry to covidDataAsOfTime instance var
        covidDataAsOfTime.put(timeStamp, new Double[]{Double.parseDouble(partialVac), Double.parseDouble(fullVac), 
        Double.parseDouble(negInfectionTests), Double.parseDouble(posInfectionTests), Double.parseDouble(deaths), 
        Double.parseDouble(hospitalized), Double.parseDouble(boosted)});
    }
    
    
    public ZipCode(Integer zipCode, Integer population) {
        this.zipCode = zipCode;
        this.population = population;
    }

    public ZipCode(Integer zipCode, ArrayList<Double> propertiesMarketValueList, ArrayList<Double> propertiesTotalLivableAreaList) {
        this.zipCode = zipCode;
        this.propertiesMarketValueList = propertiesMarketValueList;
        this.propertiesTotalLivableAreaList = propertiesTotalLivableAreaList;
    }

    //getters
    public Integer getZipCode() {
        return zipCode;
    }

    public TreeMap<String, Double[]> getCovidDataAsOfTime() {
        return this.covidDataAsOfTime;
    }

    public Double getTotalProperties() {
        return totalProperties;
    }

    public Integer getPopulation() {
        return population;
    }

    public ArrayList<Double> getPropertiesMarketValueList() {
        return propertiesMarketValueList;
    }

    public ArrayList<Double> getPropertiesLivableAreaList() {
        return propertiesTotalLivableAreaList;
    }
    
    //setters
    public void setPopulation(Integer population) {
        this.population = population;
    }
}
