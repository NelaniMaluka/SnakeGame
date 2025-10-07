package org.example;

import org.example.service.FileService;
import org.example.ui.IntroScreenUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        new Thread(FileService::getUserData).start();

        // Main game window
        JFrame frame = new JFrame("Nelani's Snake Game");

        // Snake game
        IntroScreenUI screen = new IntroScreenUI(frame);
        frame.add(screen);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
