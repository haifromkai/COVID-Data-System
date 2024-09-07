package coviddatasystem;

import coviddatasystem.datamanagement.*;
import coviddatasystem.logging.Logger;
import coviddatasystem.processor.Processor;
import coviddatasystem.ui.UserInterface;
import coviddatasystem.util.ZipCode;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

public class Main {

    /**
     * Accepts names of files as parameters and runs program
     * @param args 4 optional runtime arguments - covid, properties, population, log
     */
    public static void main(String[] args) {
        // VARIABLES
        String covidFileName= "";
        String covidFileExt = "";
        String populationFileName = "";
        String propertiesFileName = "";
        String logFileName = "";
        
        // booleans to check whether input data was provided
        boolean hasCovidData = false;
        boolean hasPopulationData = false;
        boolean hasPropertiesData = false;
        boolean hasLogFile = false;
        
        // instantiate empty zipCodes map to pass to readers if covid file isn't provided, otherwise gets updated with
        // data from covid file if provided
        Map<Integer, ZipCode> zipCodes = new TreeMap<>();

        try {
            // runtime arguments should be in form "--name=value"
            Pattern validArg = Pattern.compile("^--(?<name>.+)=(?<value>.+)$");
            
            // retrieve singleton instance of logger
            Logger logger = Logger.getInstance();

             // if arg is not one of the 4 keys in map, it is invalid
             Map<String, Integer> validArgNames = new HashMap<>();
             String[] validNames = {"covid", "properties", "population", "log"};

             for (String name: validNames) {
                 // add entries to map, with initial count of each arg name = 0
                 validArgNames.put(name, 0);
             }

             // iterate through passed in arguments
             for (String arg : args) {
                 Matcher matcher = validArg.matcher(arg);

                 if (!matcher.matches()) {
                     // check if arg has valid format
                     System.out.println("Error: " + arg + " is not formatted correctly. Arguments should be in the format \"--name=value\"");
                     return;
                 }

                 // otherwise, arg is formatted correctly and retrieve name and value
                 String name = matcher.group("name");
                 String value = matcher.group("value");

                 if (!validArgNames.containsKey(name))  {
                     // if arg name is not a key in the map (case-sensitive), it is invalid
                     System.out.println("Error: " + arg + "does not have a valid argument name. The 4 valid names are: " +
                             "'covid', 'properties', 'population', and 'log'.");
                     return;
                 }

                 // otherwise, check if name has already been used
                 if (validArgNames.get(name) != 0) {
                     System.out.println("Error: The argument name" + name + "has already been used.");
                     return;
                 } else {
                     // else, increment count
                     validArgNames.put(name, validArgNames.get(name) + 1);
                 }

                 // if arg name is "covid", check for valid file extension
                 if (name.equals("covid")) {
                     Pattern validCovidFileExt = Pattern.compile(".*\\.(?<ext>json|csv)$", Pattern.CASE_INSENSITIVE);
                     Matcher validCovidFile = validCovidFileExt.matcher(arg);

                     if (!validCovidFile.matches()) {
                         System.out.println("Error: The file extension for 'covid' is invalid. The extension must be either" +
                                 " 'json' or 'csv' (case-insensitive).)");
                         return;
                     }

                     // UPDATE FILE NAME VARIABLES IF FILE IS VALID
                     hasCovidData = true;
                     covidFileName = value;
                     covidFileExt = validCovidFile.group("ext");

                     // CHECK IF FILE EXISTS AND CAN BE OPENED
                     File covidFileObj = new File(covidFileName);
                     if (!covidFileObj.exists() || !covidFileObj.canRead()) {
                         System.out.println("Error: Covid file does not exist or cannot be read/accessed due to file " +
                                 "permissions: " + covidFileObj);
                         return;
                     }
                 }

                 // if arg name is "population", check for valid file extension
                 if (name.equals("population")) {
                     Pattern validPopulationFileExt = Pattern.compile(".*\\.(?<ext>csv)$", Pattern.CASE_INSENSITIVE);
                     Matcher validPopulationFile = validPopulationFileExt.matcher(arg);

                     if (!validPopulationFile.matches()) {
                         System.out.println("Error: The file extension for 'population' is invalid. The extension must be " +
                                 "'csv' (case-insensitive).)");
                         return;
                     }

                     // UPDATE FILE NAME VARIABLES IF FILE IS VALID
                     hasPopulationData = true;
                     populationFileName = value;

                    // CHECK IF FILE EXISTS AND CAN BE OPENED
                    File populationFileObj = new File(populationFileName);
                    if (!populationFileObj.exists() || !populationFileObj.canRead()) {
                        System.out.println("Error: Population file does not exist or cannot be read/accessed due to file" +
                                " permissions: " + populationFileObj);
                        return;
                    }
                 }


                 // if arg name is "properties", check for valid file extension
                 if (name.equals("properties")) {
                     Pattern validPropertiesFileExt = Pattern.compile(".*\\.(?<ext>csv)$", Pattern.CASE_INSENSITIVE);
                     Matcher validPropertiesFile = validPropertiesFileExt.matcher(arg);

                     if (!validPropertiesFile.matches()) {
                         System.out.println("Error: The file extension for 'properties' is invalid. The extension must be " +
                                 "'csv' (case-insensitive).)");
                         return;
                     }

                     // UPDATE FILE NAME VARIABLES IF FILE IS VALID
                     hasPropertiesData = true;
                     propertiesFileName = value;
                    
                    // CHECK IF FILE EXISTS AND CAN BE OPENED
                    File propertiesFileObj = new File(propertiesFileName);
                    if (!propertiesFileObj.exists() || !propertiesFileObj.canRead()) {
                        System.out.println("Error: Properties file does not exist or cannot be read/accessed due to file" +
                                " permissions: " + propertiesFileObj);
                        return;
                    }
                 }

                 // if arg name is "logger", set logFileName (don't need to check for valid extension)
                 if (name.equals("log")) {
                     logFileName = value;
                     logger.setOutput(logFileName);
                     hasLogFile = true;
                 }
             }
             
             StringBuilder runtimeArgs = new StringBuilder();
             
             if (hasCovidData) {
                 runtimeArgs.append(covidFileName + " ");
             }
             if (hasPopulationData) {
                 runtimeArgs.append(populationFileName + " ");
             }
             if (hasPropertiesData) {
                 runtimeArgs.append(propertiesFileName + " ");
             } 
             if (hasLogFile) {
                 runtimeArgs.append(logFileName);
             }
             
             // log provided runtime arguments (unprovided arguments are empty strings)
             logger.log(runtimeArgs.toString());
             

        // after checking that all provided runtime arguments are valid, start program logic
        
        // if covid file is provided, instantiate either csv or json covid reader and lex/parse data from covid file,
            // which returns a list of ZipCode objects
        if (hasCovidData)  {
            if (covidFileExt.equals("csv")) {
                CovidCsvReader covidCsvReader = new CovidCsvReader(covidFileName);
                covidCsvReader.readCovidCSV();
                
                // log file name right after reading
                logger.log(covidFileName);
                
                // update zipCodes map instance var with data from covid file
                zipCodes = covidCsvReader.covidParser(covidCsvReader.getRecords());
            } else if (covidFileExt.equals("json")) {
                CovidJsonReader covidJsonReader = new CovidJsonReader(covidFileName);
                // store records returned from reading json file
                List<String[]> records = covidJsonReader.readCovidJson();
                
                // log file name right after reading
                logger.log(covidFileName);
                
                // update zipCodes map instance var with data from json file
                zipCodes = covidJsonReader.covidParser(records);
            }
        } 
        
        if (hasPopulationData)  {
            // read data from population file if provided, and update zipCodes map
            PopulationCsvReader populationReader = new PopulationCsvReader(populationFileName);
            populationReader.readPopulationCSV();
            
            // log file name after reading
            logger.log(populationFileName);
            
            // update zipCode list
            populationReader.populationParser(zipCodes);
        }
        
        if (hasPropertiesData) {
            // read data from properties file if provided, and update zipCodes map
            PropertiesCsvReader propertiesReader = new PropertiesCsvReader(propertiesFileName);
            propertiesReader.readPropertiesCSV();
            
            // log file name after reading
            logger.log(propertiesFileName);
            
            // update zipCode list
            propertiesReader.propertiesParser(zipCodes);
        }
        

        // INITIALIZE PROCESSOR
        Processor processor = new Processor(zipCodes, logger);

        // CREATE UI INSTANCE (maybe pass logger as a param)
        UserInterface ui = new UserInterface(processor);

        // START UI LOGIC (DISPLAYS MENU, PROMPTS FOR USER INPUT, HANDLES USER INPUT)
        ui.start(hasCovidData, hasPopulationData, hasPropertiesData);
        } catch (IOException e) {
            System.out.println("An IOException was thrown.");
            e.printStackTrace();
        } catch (CSVFormatException e) {
            System.out.println("A CSVFormatException occurred when parsing a CSV file.");
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("A ParseException occurred when parsing the input JSON file.");
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("An NullPointerException occurred.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("An exception was thrown during program execution.");
            System.out.println("Exception class: " + e.getClass().getName());
            e.printStackTrace();
        }
    }
}
