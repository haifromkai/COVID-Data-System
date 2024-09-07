package coviddatasystem.datamanagement;

import java.io.IOException;
import java.util.*;

public class  CsvReader {
    
    protected CharacterReader reader;
    protected List<String> fieldTitles;
    protected ArrayList<String[]> records = new ArrayList<>();


    // ASCII values for escape characters
    final int COMMA = 44; // ASCII for ','
    final int DOUBLE_QUOTE = 34; // ASCII for '"'
    final int CARRIAGE_RETURN = 13; // ASCII for '\r'
    final int LINE_FEED = 10; // ASCII for '\n'

    /**
     * Instantiates char reader based on input CSV file
     * @param filename String representing name of input CSv file
     * @throws IOException when the underlying reader encountered an error
     */
    public CsvReader(String filename) throws IOException {
        this.reader = new CharacterReader(filename);
    }


    /**
     * This method uses the class's {@code CharacterReader} to read in just enough
     * characters to process a single valid CSV row, represented as an array of
     * strings where each element of the array is a field of the row. If formatting
     * errors are encountered during reading, this method throws a
     * {@code CSVFormatException} that specifies the exact point at which the error
     * occurred.
     *
     * @return a single row of CSV represented as a string array, where each
     * element of the array is a field of the row; or {@code null} when
     * there are no more rows left to be read.
     * @throws IOException        when the underlying reader encountered an error
     * @throws CSVFormatException when the CSV file is formatted incorrectly
     */
    public String[] readRow() throws IOException, CSVFormatException {
        // initialize string array to store each field
        ArrayList<String> fields = new ArrayList<>();

        // initialize string builder to store current field being read
        StringBuilder field = new StringBuilder();

        // initialize variable to store int value of current char being read
        int current = reader.read();

        if (current == -1) {
            // if file is empty or end of stream has been reached, return null
            return null;
        }

        boolean inQuote = false;
        boolean fieldStart = true;

        while (current != -1) {
            if (fieldStart && current == DOUBLE_QUOTE) {
                // if first char in field is a dquote, represents start of escaped context
                inQuote = true;
                fieldStart = false;
                // increment current to char after dquote
                current = reader.read();
                continue;
            }

            switch (current) {
                case COMMA:
                    if (inQuote) {
                        // inside quoted/escaped field, append comma to field
                        field.append((char) current);
                    } else {
                        // in non-escaped field, comma is delimiter so add prev field and reset string builder
                        fields.add(field.toString());
                        field.setLength(0);
                        fieldStart = true;
                    }
                    break;

                case DOUBLE_QUOTE:
                    if (inQuote) {
                        // validity of dquote depends on next char, so instantiate next
                        int next = reader.read();

                        if (next == DOUBLE_QUOTE) {
                            // if dquote in escaped field and next char is also a dquote, this is a valid escaped dquote
                            
                            /* don't need to evaluate second dquote in pair, so don't need to set current to next
                            at end of while loop, current = reader.read() will increment current to next char after
                            second dquote */
                            field.append((char) current);
                        } else {
                            // else, double quote is end of quoted field
                            inQuote = false;

                            // check if the next character is valid delimiter after end of escaped field
                            if (next == -1) {
                                // if dquote is end of file, add prev field and return fields
                                fields.add(field.toString());
                                return fields.toArray(new String[0]);
                            } else if (next == COMMA || next == CARRIAGE_RETURN || next == LINE_FEED) {
                                // set current to next so next iteration of loop evaluates next, not next char in reader
                                current = next;
                                continue;
                            }
                            else {
                                // invalid character after the end of a quoted field
                                throw new CSVFormatException();
                            }
                        }
                    } else {
                        // invalid double quote in unquoted field
                        throw new CSVFormatException();
                    }
                    break;

                case CARRIAGE_RETURN:
                    if (inQuote) {
                        // inside quoted field, CR is part of the field
                        field.append((char) current);
                    } else {
                        // else, CR is potential end of line
                        int next = reader.read();
                        if (next == LINE_FEED) {
                            // if next is LF, then CRLF is valid line break and end of record
                            fields.add(field.toString());
                            return fields.toArray(new String[0]);
                        } else {
                            // else, lone CR in non-escaped field is invalid
                            throw new CSVFormatException();
                        }
                    }
                    break;

                case LINE_FEED:
                    if (inQuote) {
                        // inside quoted field, LF is part of the field
                        field.append((char) current);
                    } else {
                        // in non-escaped field, LF is valid line break and end of record
                        fields.add(field.toString());
                        return fields.toArray(new String[0]);
                    }
                    break;

                default:
                    field.append((char) current);

                    if (!inQuote) {
                        int next = reader.read();
                        if (next == DOUBLE_QUOTE) {
                            // anytime a TEXTDATA char is followed by a dquote in a non-escaped field, this is invalid
                            throw new CSVFormatException();
                        } else if (next == -1) {
                            // if char is last in row and any prev escaped fields have been properly closed,
                            // add field to list and return fields
                            fields.add(field.toString());
                            return fields.toArray(new String[0]);
                        } else {
                            // move to next character
                            current = next;
                            continue;
                        }
                    }
                    break;
            }
            // increment current to next char in reader
            current = reader.read();
        }

        if (inQuote) {
            // if inQuote == true by end of row, that means that an escaped field was not properly closed
            throw new CSVFormatException();
        }

        return fields.toArray(new String[0]);
    }

    /**
     * Method to check if a string can be parsed as a double
     */
    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public ArrayList<String[]> getRecords() {
        return this.records;
    }
}