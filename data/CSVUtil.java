package data;

import java.io.*;
import java.util.*;

/**
 * Utility class for CSV file operations
 * Handles reading and writing CSV files with proper escaping
 */
public class CSVUtil {
    private static final String CSV_SEPARATOR = ",";
    private static final String CSV_QUOTE = "\"";

    /**
     * Read CSV file and return list of string arrays
     */
    public static List<String[]> readCSV(String filename) throws IOException {
        List<String[]> records = new ArrayList<>();
        File file = new File(filename);

        if (!file.exists()) {
            return records; // Return empty list if file doesn't exist
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = parseCSVLine(line);
                records.add(values);
            }
        }

        return records;
    }

    /**
     * Write list of string arrays to CSV file
     */
    public static void writeCSV(String filename, List<String[]> records) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            for (String[] record : records) {
                pw.println(formatCSVLine(record));
            }
        }
    }

    /**
     * Parse a CSV line handling quotes and escaping
     */
    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    // Escaped quote
                    currentField.append('\"');
                    i++; // Skip next quote
                } else {
                    // Toggle quote state
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // Field separator
                result.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }

        // Add last field
        result.add(currentField.toString());

        return result.toArray(new String[0]);
    }

    /**
     * Format array of strings as CSV line with proper escaping
     */
    private static String formatCSVLine(String[] fields) {
        StringBuilder line = new StringBuilder();

        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                line.append(CSV_SEPARATOR);
            }

            String field = fields[i] != null ? fields[i] : "";

            // Quote field if it contains comma, quote, or newline
            if (field.contains(CSV_SEPARATOR) || field.contains(CSV_QUOTE) || field.contains("\n")) {
                line.append(CSV_QUOTE);
                line.append(field.replace(CSV_QUOTE, CSV_QUOTE + CSV_QUOTE));
                line.append(CSV_QUOTE);
            } else {
                line.append(field);
            }
        }

        return line.toString();
    }
}