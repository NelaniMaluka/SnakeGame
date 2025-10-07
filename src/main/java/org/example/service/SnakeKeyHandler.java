package org.example.service;

import org.example.ui.SnakeGameUI;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SnakeKeyHandler extends KeyAdapter {
    private final SnakeGameUI game;

    public SnakeKeyHandler(SnakeGameUI game) {
        this.game = game;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        game.handleKeyPress(e);
    }

}

