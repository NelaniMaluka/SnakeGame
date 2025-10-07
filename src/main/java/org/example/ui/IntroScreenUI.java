package org.example.ui;

import javax.swing.*;
import java.awt.*;

public class IntroScreenUI extends JPanel{
    // Window dimensions
    int boardWidth = 600;
    int boardHeight = boardWidth;

    public IntroScreenUI(JFrame frame) {
        setBackground(Color.darkGray);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(boardWidth, boardHeight));

        // Welcome label with styled HTML text
        String introText = "<html><div style='width:380px; text-align:center;"
                + "font-family:Arial;'>"
                + "Welcome to Nelani's Game!<br>Get ready to play Snake."
                + "</div></html>";

        JLabel label = new JLabel(introText, SwingConstants.CENTER);
        label.setBackground(Color.darkGray);
        label.setForeground(Color.white);
        label.setFont(new Font("Arial", Font.BOLD, 28));
        label.setOpaque(true);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Start Game button
        JButton button = new JButton("Start Game");
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(150, 25));

        // On click → gather settings and switch to game screen
        button.addActionListener(e -> {switchToGame(frame, boardWidth, boardHeight);});

        // Start Game button
        JButton statsButton = new JButton("Stats");
        statsButton.setFont(new Font("Arial", Font.BOLD, 15));
        statsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsButton.setMaximumSize(new Dimension(150, 25));

        // On click → gather settings and switch to game screen
        statsButton.addActionListener(e -> {switchToStats(frame);});

        add(Box.createVerticalStrut(130));
        add(label);
        add(Box.createVerticalStrut(80));
        add(button);
        add(Box.createVerticalStrut(40));
        add(statsButton);
    }

    private void switchToGame(JFrame frame, int boardWidth, int boardHeight) {
        SnakeGameUI snakeGameUI = new SnakeGameUI(25, boardWidth, boardHeight, frame);

        frame.getContentPane().removeAll();
        frame.add(snakeGameUI);
        frame.revalidate();
        frame.repaint();

        SwingUtilities.invokeLater(snakeGameUI::requestFocusInWindow);
    }

    private void switchToStats(JFrame frame) {
        UserStatsUI userStatsUI = new UserStatsUI(frame);

        frame.getContentPane().removeAll();
        frame.add(userStatsUI);
        frame.revalidate();
        frame.repaint();

        SwingUtilities.invokeLater(userStatsUI::requestFocusInWindow);
    }

}
