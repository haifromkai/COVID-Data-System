package coviddatasystem.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {

    private String fileOut;
    
    private PrintWriter out;
    
    private static final Logger logInstance = new Logger();

    // private constructor
    private Logger() {}

    // getters
    public String getFileOut() {
        return this.fileOut;
    }

    /**
     * Returns Singleton instance of Logger class
     * @return logInstance
     */
    public static Logger getInstance() {return logInstance;}

    /**
     * Method to set/change output file to write to
     * @param fileName string representing name of file
     * @throws IOException error creating, opening, or closing file 
     */
    public void setOutput(String fileName) throws IOException {
        // if there is an existing log PrintWriter object, close it
        if (out != null) {
            out.flush();
            out.close();
        }

        // update file to be written to
        this.fileOut = fileName;
        // creates file if it doesn't exist or opens in append mode with automatic flushing after print statements
        out = new PrintWriter(new FileWriter(fileOut, true), true);
    }

    /**
     * Method to log a command line argument, name of input file when opened for reading, or a response from the user
     */
    public void log(String event) {
        if (out != null) {
            // prepend each event with a timestamp
            out.println(System.currentTimeMillis() + " " + event);
        } else {
            // if no output log file has been set, log to System.err
            System.err.println(System.currentTimeMillis() + " " + event);
        }
    }
}
