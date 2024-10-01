package src;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageDatabase {
    private static final String MESSAGES_FILE_PATH = "messages.csv";

    public static void writeMessages(List<String> messages) {
        // Append messages to the CSV file
        try (CSVWriter writer = new CSVWriter(new FileWriter(MESSAGES_FILE_PATH, true))) {
            for (String message : messages) {
                writer.writeNext(new String[]{message});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readMessages() {
        List<String> messages = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(MESSAGES_FILE_PATH))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length > 0) {
                    messages.add(nextLine[0]);
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public static void clearMessages() {
        // Clear the content of the CSV file
        try (CSVWriter writer = new CSVWriter(new FileWriter(MESSAGES_FILE_PATH))) {
            writer.writeNext(new String[]{""});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
