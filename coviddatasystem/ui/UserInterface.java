package coviddatasystem.ui;

import coviddatasystem.logging.Logger;
import coviddatasystem.processor.Processor;
import coviddatasystem.util.ZipCode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.ArrayList;

/**
 * UserInterface is a class in the UI Tier.
 * 
 * Its function is to display the menu of possible actions and prompt the user 
 * to specify the action to be performed. Action can be selected by typing the
 * action number and hitting return.
 * 
 * List of possible actions:
 * (0) Exit program
 * (1) Show available actions
 * (2) Show total population for all ZIP Codes
 * (3) Show total vaccinations per capita for each ZIP Code for the specified date
 * (4) Show average market value for properties in a specified ZIP Code
 * (5) Show average total livable area for properties in a specified ZIP Code
 * (6) Show total market value of properties, per capita, for a specified ZIP Code
 * (7) Show results of our custom feature that displays the number of unvaccinated persons per 100 square feet of
 * livable property area for all ZIP Codes
 */

public class UserInterface {
    
    private Processor processor;
    private Logger logger = Logger.getInstance();
    private Scanner scanner;
    private Map<Integer, ZipCode> zipCodes;
    

    // CONSTRUCTOR
    public UserInterface(Processor processor) {
        this.processor = processor;
        this.zipCodes = processor.getZipCodes();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays menu, gets user input, and processes user input.
     */
    public void start(boolean hasCovidData, boolean hasPopulationData, boolean hasPropertiesData) {
        int input;

        do {
            displayMenu();
            input = getValidMenuChoice();
            handleInput(input, hasCovidData, hasPopulationData, hasPropertiesData);
        } 
        while (input != 0);
    }



    /**
     * Prints menu onto the terminal. Flushes stream.
     */
    private void displayMenu() {
        System.out.println("\nPlease enter an action number and hit return.");
        // call showAvailableActions to display menu options
        showAvailableActions();
        System.out.print("> ");
        System.out.flush();
    }

    /**
     * Use switch cases to handle user's action number input accordingly.
     * @param input from the user.
     */
    private void handleInput(int input, boolean hasCovidData, boolean hasPopulationData, boolean hasPropertiesData) {

        switch (input) {
            // Exit program
            case 0:
                // System.out.print("BEGIN OUTPUT");
                System.out.println("Exiting the program...");
                // System.out.print("END OUTPUT");
                break;
            
            // Show available actions based on what run-time arguments are given
            case 1:
                System.out.println("\nBEGIN OUTPUT");
                System.out.println("0");
                System.out.println("1");
                if (hasPopulationData) {
                    System.out.println("2");
                }
                if (hasCovidData && hasPopulationData) {
                    System.out.println("3");
                }
                if (hasPropertiesData) {
                    System.out.println("4");
                    System.out.println("5");
                }
                if (hasPropertiesData && hasPopulationData) {
                    System.out.println("6");
                }
                if (hasCovidData && hasPopulationData && hasPropertiesData) {
                    System.out.println("7");
                }
                System.out.println("END OUTPUT");
                break;

            // Show total population for all ZIP Codes
            case 2:
                if (!hasPopulationData) {
                    // System.out.print("BEGIN OUTPUT");
                    System.out.println("No population data provided in the runtime arguments.");
                    // System.out.print("END OUTPUT");
                    break;
                }
                showTotalPopulation();
                break;
            
            // Show total vaccinations per capita for each ZIP Code for the specified date
            case 3:
                if (!hasCovidData || !hasPopulationData) {
                    // System.out.print("BEGIN OUTPUT");
                    System.out.println("Insufficient data provided in the runtime arguments, requires both covid and " +
                            "population data.");
                    // System.out.print("END OUTPUT");
                    break;
                }
                showVaccinationsPerCapita();
                break;

            // Show average market value for properties in a specified ZIP Code
            case 4:
                if (!hasPropertiesData) {
                    // System.out.print("BEGIN OUTPUT");
                    System.out.println("No properties data provided in the runtime arguments.");
                    // System.out.print("END OUTPUT");
                    break;
                }
                showAverageMarketValue();
                break;
            
            // Show average total livable area for properties in a specified ZIP Code
            case 5:
                if (!hasPropertiesData) {
                    // System.out.print("BEGIN OUTPUT");
                    System.out.println("No properties data provided in the runtime arguments.");
                    // System.out.print("END OUTPUT");
                    break;
                }
                showAverageLivableArea();
                break;

            // Show total market value of properties, per capita, for a specified ZIP Code
            case 6:
                if (!hasPropertiesData || !hasPopulationData) {
                    // System.out.print("BEGIN OUTPUT");
                    System.out.println("Insufficient data provided in the runtime arguments, requires both properties" +
                            "and population data.");
                    // System.out.print("END OUTPUT");
                    break;
                }
                showMarketValuePerCapita();
                break;
            
            // CUSTOM FEATURE :D
            case 7:
                if (!hasCovidData || !hasPropertiesData || !hasPopulationData) {
                    // System.out.print("BEGIN OUTPUT");
                    System.out.println("Insufficient data provided in the runtime arguments, requires covid, properties, " +
                            "and population data.");
                    // System.out.print("END OUTPUT");
                    break;
                }
                showUnvaccinatedPersonsPerTotalLiveableArea();
                break;

            default:
                // System.out.print("BEGIN OUTPUT");
                System.out.println("Invalid input. Please select a valid action number.");
                // System.out.print("END OUTPUT");
                break;
        }
    }

    /**
     * Shows the available actions.
     */
    private void showAvailableActions() {
        System.out.println("(0) Exit program");
        System.out.println("(1) Show available actions");
        System.out.println("(2) Show total population for all ZIP Codes");
        System.out.println("(3) Show total vaccinations per capita for each ZIP Code for the specified date");
        System.out.println("(4) Show average market value for properties in a specified ZIP Code");
        System.out.println("(5) Show average total livable area for properties in a specified ZIP Code");
        System.out.println("(6) Show total market value of properties, per capita, for a specified ZIP Code");
        System.out.println("(7) Show number of unvaccinated persons per 100 square feet of livable property area for all ZIP Codes");
    }

    /**
     * Shows the total population for all ZIP Codes.
     */
    private void showTotalPopulation() {
        System.out.println("\nBEGIN OUTPUT");
        System.out.println(processor.getTotalPopulation());
        System.out.println("END OUTPUT");
    }

    /**
     * Shows the total vaccinations per capita for each ZIP Code for a specified date
     */
    private void showVaccinationsPerCapita() {
        // prompt user to select partial or total vaccinations, check if choice is valid, passes as parameter for processor's getter method
        Boolean partialOrTotal = getPartialOrTotal();
        // prompt user to enter a date, check if date is valid, passes date as parameter for processor's getter method
        String date = getValidDate();

        TreeMap<Integer, Double> vacsPerCapita = processor.getVaccinationsPerCapita(date, partialOrTotal);

        System.out.println("\nBEGIN OUTPUT");
        
        for (Entry<Integer, Double> entry : vacsPerCapita.entrySet()) {
            Integer key = entry.getKey();
            Double value = entry.getValue();
            
            if (value != 0.0) {
                // only print out values when value is not 0
                System.out.println(key + " " + String.format("%.4f", value));
            }
        }
        System.out.println("END OUTPUT");
    }

    /**
     * Shows average market value for properties for a specified ZIP Code
     */
    private void showAverageMarketValue() {
        // prompt user to enter a zip code, check if zip code is valid, passes zip code as parameter for processor's getter method
        String zipCode = getValidZipCode();
        System.out.println("\nBEGIN OUTPUT");
        System.out.println(processor.getAverageMarketValue(zipCode));
        System.out.println("END OUTPUT");
    }

    /**
     * Shows average total livable area for properties for a specified ZIP Code
     */
    private void showAverageLivableArea() {
        // prompt user to enter a zip code, check if zip code is valid, passes zip code as parameter for processor's getter method
        String zipCode = getValidZipCode();
        System.out.println("\nBEGIN OUTPUT");
        System.out.println(processor.getAverageLivableArea(zipCode));
        System.out.println("END OUTPUT");
    }

    /**
     * Shows total market value of properties, per capita, for a specified ZIP Code
     */
    private void showMarketValuePerCapita() {
        // prompt user to enter a zip code, check if zip code is valid, passes zip code as parameter for processor's getter method
        String zipCode = getValidZipCode();
        System.out.println("\nBEGIN OUTPUT");
        System.out.println(processor.getMarketValuePerCapita(zipCode));
        System.out.println("END OUTPUT");
    }

    /**
     * Shows the amount of unvaccinated people per 100 square feet for each zip code
     */
    private void showUnvaccinatedPersonsPerTotalLiveableArea() {
        System.out.println("\nBEGIN OUTPUT");
        
        // retrieve map from processor
        Map<Integer, Double> results = processor.getUnvaccinatedPersonsPerTotalLiveableArea();
        
        for (Map.Entry<Integer, Double> entry : results.entrySet()) {
            // print out zip code value and number of unvacced persons per 100 sq ft
            System.out.println(entry.getKey() + ": " + String.format("%.4f", entry.getValue()));
        }
        
        System.out.println("END OUTPUT");
    }

    // HELPER METHODS FOR INPUT VALIDATION

    /**
     * Prompts for and validates user input.
     * @return the valid user input
     */
    private int getValidMenuChoice() {
        while (true) {
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();

                // log the input as an event
                logger.log(input);

                try {
                    int choice = Integer.parseInt(input);
                    if (choice >= 0 && choice <= 7) {
                        return choice;
                    } else {
                        System.err.println("Invalid choice. Please enter a number between 0 and 7.");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid input. Please enter a valid integer.");
                }
                System.out.println("> ");
                System.out.flush();
            }
        }
    }

    /**
     * Gets a valid ZIP Code from the user.
     * @return the valid ZIP Code
     */
    private String getValidZipCode() {
        while (true) {
            System.out.println("Please enter a ZIP Code: ");
            
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();

                // log the input as an event
                logger.log(input);

                if (input.matches("\\d{5}")) {
                    return input;
                } else {
                    System.err.println("Invalid ZIP Code. Please enter a 5-digit number.");
                }
            }
            System.out.println("> ");
        }
    }

    /**
     * Gets a valid date from the user.
     * @return the valid date
     */
    private String getValidDate() {
        
        while (true) {
            System.out.println("Please enter a date in format [YYYY-MM-DD]: ");
            
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();

                // log the input as an event
                logger.log(input);

                if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    return input;
                } else {
                    System.err.println("Invalid date format. Please enter a date in YYYY-MM-DD format.");
                }
            }
            System.out.println("> ");
        }
        
            
    }

    /**
     * Prompts the user for a valid choice between partial and total vaccinations.
     * @return true if partial, false if total
     */
    private Boolean getPartialOrTotal() {
        
        while (true) {
            System.out.println("Would you like to see the total number of partial or full vaccinations? " +
                    "Enter 'partial' or 'full': ");
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();

                // log the input as an event
                logger.log(input);

                if (input.equals("partial")) {
                    return true;
                } else if (input.equals("full")) {
                    return false;
                } else {
                    System.err.println("Invalid input. Please enter 'partial' or 'full'.");
                }
            }
            System.out.println("> ");
        }
    }
    
}
