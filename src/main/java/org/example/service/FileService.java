package org.example.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import lombok.extern.log4j.Log4j2;
import org.example.model.SessionData;
import org.example.model.UserAnalytics;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class FileService {
    private static final String FILE_PATH = "data/snakeData.csv";
    private static final String[] HEADERS = {"Id", "Score", "Snake-Length", "Food-Eaten", "Time-Spent", "Moves"};

    private static final List<SessionData> dataList = new ArrayList<>();
    public static UserAnalytics userAnalytics = new UserAnalytics();

    /**
     * Writes session data to a CSV file asynchronously.
     * Does not throw exceptions; all errors are logged.
     */
    public static void writeData(SessionData data) {
        File file = new File(FILE_PATH);
        log.info("Starting to write session data on thread: {}", Thread.currentThread().getName());

        try {
            // Simulate delay
            Thread.sleep(2000);

            boolean fileExists = file.exists();
            int nextId = 0;

            if (fileExists) {
                // Determine the next ID based on existing file content
                try (CSVReader reader = new CSVReader(new FileReader(file))) {
                    List<String[]> rows = reader.readAll();
                    if (rows.size() > 1) {
                        String[] lastRow = rows.getLast();
                        nextId = Integer.parseInt(lastRow[0]) + 1;
                    }
                } catch (Exception e) {
                    log.error("Error reading CSV file", e);
                }
            }

            // Write the session data to the CSV file
            try (CSVWriter writer = new CSVWriter(new FileWriter(file, true))) {
                if (!fileExists) {
                    writer.writeNext(HEADERS); // Write headers for a new file
                    log.info("Header written to new CSV file");
                }

                data.setId(nextId);
                writer.writeNext(data.toCsvRow());
                log.info("Session data written successfully: {}", data);

                dataList.add(data);
                calculateUserAnalytics();

            } catch (Exception e) {
                log.error("Error writing to CSV file '{}'", FILE_PATH, e);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted while writing session data", e);
        }

        log.info("Finished writing session data on thread: {}", Thread.currentThread().getName());
    }

    /**
     * Reads session data from the CSV file and populates the dataList.
     * Errors are logged; no exceptions are thrown.
     */
    public static void getUserData() {
        File file = new File(FILE_PATH);

        try {
            // Simulate delay (optional)
            Thread.sleep(1000);

            // Check if file exists
            boolean fileExists = file.exists();
            if (!fileExists) {
                log.warn("CSV file '{}' does not exist.", FILE_PATH);
                return; // No data to read
            }

            // Open CSV file and read all rows
            try (CSVReader reader = new CSVReader(new FileReader(file))) {
                List<String[]> rows = reader.readAll();

                // Skip header row (index 0) and process data rows
                for (int i = 1; i < rows.size(); i++) {
                    String[] row = rows.get(i);
                    try {
                        // Parse each column and remove quotes
                        long id = Long.parseLong(row[0].replace("\"", "").trim());
                        long score = Long.parseLong(row[1].replace("\"", "").trim());
                        int length = Integer.parseInt(row[2].replace("\"", "").trim());
                        int foodEaten = Integer.parseInt(row[3].replace("\"", "").trim());
                        long timeSpentMillis = Integer.parseInt(row[4].replace("\"", "").trim());
                        int moves = Integer.parseInt(row[5].replace("\"", "").trim());
                        LocalDateTime date = LocalDateTime.parse(row[6].replace("\"", "").trim());

                        // Create SessionData object and add to list
                        SessionData sessionData = SessionData.builder()
                                .id(id)
                                .score(score)
                                .length(length)
                                .foodEaten(foodEaten)
                                .moves(moves)
                                .timeSpentMillis(timeSpentMillis)
                                .date(date)
                                .build();

                        dataList.add(sessionData);
                        calculateUserAnalytics();

                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException | DateTimeParseException ex) {
                        // Log individual row parsing errors without stopping the process
                        log.error("Error parsing row {}: {}", i, row, ex);
                    }
                }
            } catch (IOException | CsvException e) {
                log.error("Error reading CSV file '{}'", FILE_PATH, e);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted while reading session data", e);
        } catch (Exception e) {
            // Catch-all for unexpected errors
            log.error("Unexpected error while reading session data", e);
        }
    }

    /**
     * Clears all session data in the CSV file except for the header (first row).
     * If the file doesn't exist or is empty, no action is taken.
     * Errors are logged; no exceptions are thrown.
     */
    public static void clearDataFile() {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            log.warn("File '{}' does not exist. Nothing to clear.", FILE_PATH);
            return;
        }

        try {
            // Read all lines
            List<String> lines = Files.readAllLines(file.toPath());

            // Keep only the first line (header)
            if (!lines.isEmpty()) {
                String header = lines.getFirst();

                // Overwrite the file with only the header
                try (FileWriter writer = new FileWriter(file, false)) { // false = overwrite
                    writer.write(header + System.lineSeparator());
                }

                dataList.clear();
                userAnalytics = new UserAnalytics();
                log.info("Cleared all session data except the header in '{}'", FILE_PATH);
            } else {
                log.warn("File '{}' was empty — nothing to clear.", FILE_PATH);
            }

        } catch (IOException e) {
            log.error("Error while clearing data file '{}'", FILE_PATH, e);
        }
    }

    /**
     * Calculates user analytics based on all saved game sessions in dataList.
     * Updates the global userAnalytics object with totals, averages, longest, and shortest metrics.
     * Log key steps and any potential issues.
     */
    private static void calculateUserAnalytics() {
        if (dataList.isEmpty()) {
            log.warn("No session data available to calculate analytics.");
            return;
        }

        // Totals
        long totalMoves = 0;
        long totalFoodEaten = 0;
        long totalTimeSpent = 0;
        long totalLength = 0;

        // Longest values
        long longestLength = 0;
        long longestFoodEaten = 0;
        long longestMoves = 0;
        long longestTimeSpent = 0;

        // Shortest values (initialize with first session)
        long shortestLength = dataList.getFirst().getLength();
        long shortestFoodEaten = dataList.getFirst().getFoodEaten();
        long shortestMoves = dataList.getFirst().getMoves();
        long shortestTimeSpent = dataList.getFirst().getTimeSpentMillis();

        // Iterate over all sessions to calculate totals, longest, and shortest metrics
        for (SessionData data : dataList) {
            // Update totals
            totalMoves += data.getMoves();
            totalFoodEaten += data.getFoodEaten();
            totalTimeSpent += data.getTimeSpentMillis();
            totalLength += data.getLength();

            // Update longest values
            if (data.getLength() > longestLength) longestLength = data.getLength();
            if (data.getFoodEaten() > longestFoodEaten) longestFoodEaten = data.getFoodEaten();
            if (data.getMoves() > longestMoves) longestMoves = data.getMoves();
            if (data.getTimeSpentMillis() > longestTimeSpent) longestTimeSpent = data.getTimeSpentMillis();

            // Update shortest values
            if (data.getLength() < shortestLength) shortestLength = data.getLength();
            if (data.getFoodEaten() < shortestFoodEaten) shortestFoodEaten = data.getFoodEaten();
            if (data.getMoves() < shortestMoves) shortestMoves = data.getMoves();
            if (data.getTimeSpentMillis() < shortestTimeSpent) shortestTimeSpent = data.getTimeSpentMillis();
        }

        int totalGames = dataList.size();
        log.info("Calculating analytics for {} game sessions.", totalGames);

        // Set totals
        userAnalytics.setTotalGames(totalGames);
        userAnalytics.setTotalMoves(totalMoves);
        userAnalytics.setTotalFoodEaten(totalFoodEaten);
        userAnalytics.setTotalTimeSpent(totalTimeSpent);

        // Calculate and set averages
        userAnalytics.setAverageLength(totalLength / totalGames);
        userAnalytics.setAverageMoves(totalMoves / totalGames);
        userAnalytics.setAverageFoodEaten(totalFoodEaten / totalGames);
        userAnalytics.setAverageTimeSpent(totalTimeSpent / totalGames);

        // Set longest metrics
        userAnalytics.setLongestLength(longestLength);
        userAnalytics.setLongestFoodEaten(longestFoodEaten);
        userAnalytics.setLongestMoves(longestMoves);
        userAnalytics.setLongestTimeSpent(longestTimeSpent);

        // Set shortest metrics
        userAnalytics.setShortestLength(shortestLength);
        userAnalytics.setShortestFoodEaten(shortestFoodEaten);
        userAnalytics.setShortestMoves(shortestMoves);
        userAnalytics.setShortestTimeSpent(shortestTimeSpent);

        log.info("User analytics calculated successfully: {}", userAnalytics);
    }

}
