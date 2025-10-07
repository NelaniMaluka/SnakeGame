package org.example.ui;

import org.example.model.UserAnalytics;
import org.example.service.FileService;
import org.example.utils.Formatter;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple UI panel that displays player statistics using the UserAnalytics model.
 * It retrieves data from FileService and presents it in a clean Swing layout.
 */
public class UserStatsUI extends JPanel {

    public UserStatsUI(JFrame frame) {
        // Safely fetch analytics data (fallback to empty data if null)
        UserAnalytics userData = FileService.userAnalytics;

        setLayout(new GridLayout(0, 1, 10, 10)); // one column, vertical spacing
        setBackground(Color.DARK_GRAY);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== HEADER =====
        JLabel statsLabel = createLabel("Player Statistics", 22, Color.CYAN, SwingConstants.CENTER);
        add(statsLabel);

        // ===== TOTALS =====
        add(createSectionLabel("Totals"));
        add(createDataLabel("Total Games: " + userData.getTotalGames()));
        add(createDataLabel("Total Moves: " + userData.getTotalMoves()));
        add(createDataLabel("Total Food Eaten: " + userData.getTotalFoodEaten()));
        add(createDataLabel("Total Time Spent (ms): " + Formatter.formatTime(userData.getTotalTimeSpent())));

        // ===== AVERAGES =====
        add(createSectionLabel("Averages"));
        add(createDataLabel("Average Moves per Game: " + userData.getAverageMoves()));
        add(createDataLabel("Average Food per Game: " + userData.getAverageFoodEaten()));
        add(createDataLabel("Average Time (ms): " + Formatter.formatTime(userData.getAverageTimeSpent())));

        // ===== LONGEST =====
        add(createSectionLabel("Longest Stats"));
        add(createDataLabel("Longest Length: " + userData.getLongestLength()));
        add(createDataLabel("Longest Moves: " + userData.getLongestMoves()));
        add(createDataLabel("Longest Food Eaten: " + userData.getLongestFoodEaten()));
        add(createDataLabel("Longest Time (ms): " + Formatter.formatTime(userData.getLongestTimeSpent())));

        // ===== SHORTEST =====
        add(createSectionLabel("Shortest Stats"));
        add(createDataLabel("Shortest Length: " + userData.getShortestLength()));
        add(createDataLabel("Shortest Moves: " + userData.getShortestMoves()));
        add(createDataLabel("Shortest Food Eaten: " + userData.getShortestFoodEaten()));
        add(createDataLabel("Shortest Time (ms): " + Formatter.formatTime(userData.getShortestTimeSpent())));

        // ===== BACK BUTTON =====
        JButton backButton = new JButton("Back");
        backButton.setMaximumSize(new Dimension(150, 25));
        backButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new IntroScreenUI(frame));
            frame.revalidate();
            frame.repaint();
        });
        add(backButton);

        // ===== RESET BUTTON =====
        JButton resetButton = new JButton("Reset");
        resetButton.setMaximumSize(new Dimension(150, 25));
        resetButton.addActionListener(e -> {
            FileService.clearDataFile();

            // Rebuild panel to reflect new data
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new UserStatsUI(frame));
            frame.revalidate();
            frame.repaint();
        });
        add(resetButton);
    }

    // Utility method to create consistent labels
    private JLabel createLabel(String text, int size, Color color, int align) {
        JLabel label = new JLabel(text, align);
        label.setForeground(color);
        label.setFont(new Font("Arial", Font.BOLD, size));
        return label;
    }

    private JLabel createSectionLabel(String text) {
        return createLabel(text, 18, Color.ORANGE, SwingConstants.LEFT);
    }

    private JLabel createDataLabel(String text) {
        return createLabel(text, 14, Color.WHITE, SwingConstants.LEFT);
    }
}
