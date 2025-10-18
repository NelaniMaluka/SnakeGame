package org.example.ui;

import org.example.service.SnakeKeyHandler;
import org.example.model.SessionData;
import org.example.model.Tile;
import org.example.service.FileService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGameUI extends JPanel implements ActionListener {
    // Game Components
    private final int blockSize;
    private final int panelWidth;
    private final int panelHeight;
    private final Random random = new Random();
    private long startTime;

    // Game
    Tile snakeHead;
    Tile food;
    ArrayList<Tile> snakeBody;
    boolean gameOver = false;
    int moves = 0;

    // Game Logic
    Timer gameLoop;
    int velocityX;
    int velocityY;
    String gameMode;

    int timeLimitSeconds;
    int foodToEat;
    int tickCounter;
    int foodEaten;

    private final JFrame frame; // for going back to intro panel

    public SnakeGameUI(int blockSize, int panelWidth, int panelHeight, String gameMode) {
        this(blockSize, panelWidth, panelHeight,  gameMode, null);
    }

    public SnakeGameUI(int blockSize, int panelWidth, int panelHeight, String gameMode, JFrame frame) {
        this.blockSize = blockSize;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.frame = frame;
        this.gameMode = gameMode;

        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(this.panelWidth, this.panelHeight));

        // Generate a random snake head position
        snakeHead = new Tile(random.nextInt((panelWidth / blockSize) - 10 + 1), random.nextInt((panelHeight / blockSize) - 10 + 1));
        this.snakeBody = new ArrayList<>();
        //this.snakeBody.add(snakeHead);
        placeFood();

        gameLoop = new Timer(100, this);
        velocityX = 0;
        velocityY = 0;
        startTime = System.currentTimeMillis();
        gameLoop.start();
        generateTimeModeValues();

        // Listen for keyPress
        addKeyListener(new SnakeKeyHandler(this));
        setFocusable(true);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Grid
        //  for (int i = 0; i < panelWidth/blockSize; i++) {
        //      g.drawLine(i * blockSize, 0, i * blockSize, panelHeight);
        //      g.drawLine(0,i * blockSize, panelWidth,  i * blockSize);
        //  }

        // Food
        g.setColor(Color.RED);
        // g.fillRect(food.getX() * blockSize, food.getY() * blockSize, blockSize, blockSize);
        g.fill3DRect(food.getX() * blockSize, food.getY() * blockSize, blockSize, blockSize, true);

        // Set Snake Head
        g.setColor(Color.GREEN);
        // g.fillRect(snakeHead.getX() * blockSize, snakeHead.getY() * blockSize, blockSize, blockSize);
        g.fill3DRect(snakeHead.getX() * blockSize, snakeHead.getY() * blockSize, blockSize, blockSize, true);

        // Snake body
        for (Tile snakePart : snakeBody) {
            // g.fillRect(snakePart.getX() * blockSize, snakePart.getY() * blockSize, blockSize, blockSize);
            g.fill3DRect(snakePart.getX() * blockSize, snakePart.getY() * blockSize, blockSize, blockSize, true);
        }

        // Score
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver) {
            g.setColor(Color.RED);
            g.drawString("Game Over: " + String.valueOf(snakeBody.size()), blockSize - 16, blockSize);
            showGameOverButtons();
        } else {
            g.drawString("Score: " + String.valueOf(snakeBody.size()), blockSize - 16, blockSize);
            if (gameMode.equals("Timed")) {
                int minutes = timeLimitSeconds / 60;
                int seconds = timeLimitSeconds % 60;
                String formattedTime = String.format("%02d:%02d", minutes, seconds);

                g.drawString("Get: " + String.valueOf(foodToEat) + " Blocks in Time: " + formattedTime, blockSize + 320, blockSize);
            }
        }
    }

    public void placeFood() {
        int gridWidth = panelWidth / blockSize;
        int gridHeight = panelHeight / blockSize;

        boolean validPosition;
        do {
            int x = random.nextInt(gridWidth);
            int y = random.nextInt(gridHeight);

            // Check that it doesn't overlap head or body
            validPosition = true;

            if (x == snakeHead.getX() && y == snakeHead.getY()) {
                validPosition = false;
            } else {
                for (Tile segment : snakeBody) {
                    if (segment.getX() == x && segment.getY() == y) {
                        validPosition = false;
                        break;
                    }
                }
            }

            if (validPosition) {
                food = new Tile(x, y);
            }
        } while (!validPosition);
    }

    public void eatFood() {
        if (food.getX() == snakeHead.getX() && food.getY() == snakeHead.getY()) {
            // Add new segment at tail (clone the last body segment or head if first segment)
            Tile newSegment;
            if (snakeBody.isEmpty()) {
                newSegment = new Tile(snakeHead.getX(), snakeHead.getY());
            } else {
                Tile tail = snakeBody.get(snakeBody.size() - 1);
                newSegment = new Tile(tail.getX(), tail.getY());
            }
            snakeBody.add(newSegment);

            placeFood(); // Generate new food
            foodEaten++;
        }
    }

    private void generateTimeModeValues() {
        Random random = new Random();
        foodToEat = random.nextInt(20) + 1; // 1 to 20 fruits

        int gridWidth = panelWidth / blockSize;
        int gridHeight = panelHeight / blockSize;

        // Base time from grid size
        int baseTime = (gridWidth * gridHeight) / (10 * 2);

        // Scale time based on valToGet (e.g., +5 seconds per fruit)
        timeLimitSeconds = baseTime + (foodToEat * 5);
        foodEaten = 0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eatFood();
        move();
        repaint();

        if (gameMode.equals("Timed")) {
            if (timeLimitSeconds <= 0){
                gameOver = true;
            }

            if (foodEaten == foodToEat) {
                generateTimeModeValues();
            }

            tickCounter++;
            if (tickCounter >= 10) { // 10 ticks × 100ms = 1 second
                timeLimitSeconds--;
                tickCounter = 0;
            }
        }

        if (gameOver) {
            gameLoop.stop();
            long elapsedTime = System.currentTimeMillis() - startTime;

            SessionData sessionData = SessionData.builder()
                    .score(snakeBody.size())
                    .length(snakeBody.size())
                    .foodEaten(snakeBody.size())
                    .timeSpentMillis(elapsedTime)
                    .moves(moves)
                    .build();
            new Thread(() -> FileService.writeData(sessionData)).start();
        }

    }

    private void move() {
        // Save old head position
        int oldHeadX = snakeHead.getX();
        int oldHeadY = snakeHead.getY();

        // Move head
        snakeHead.setX(snakeHead.getX() + velocityX);
        snakeHead.setY(snakeHead.getY() + velocityY);

        // Move body segments
        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) {
                snakePart.setX(oldHeadX);
                snakePart.setY(oldHeadY);
            } else {
                Tile prevTile = snakeBody.get(i - 1);
                snakePart.setX(prevTile.getX());
                snakePart.setY(prevTile.getY());
            }
        }

        checkCollision();
    }

    public void checkCollision() {
        // Check if the snake head collides with the wall
        if (snakeHead.getY() < 0 || snakeHead.getY() >= panelWidth/blockSize ||
                snakeHead.getX() < 0 || snakeHead.getX() >= panelWidth/blockSize) {
            gameOver = true;
        }

        // Check if the snake head collides with its body
        for (Tile snakePart: snakeBody) {
            if (snakePart.getX() == snakeHead.getX() && snakePart.getY() == snakeHead.getY()) {
                gameOver = true;
            }
        }
    }

    public void handleKeyPress(KeyEvent e) {
        int key = e.getKeyCode();
        moves++;

        // Up or W
        if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        }
        // Down or S
        else if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        }
        // Left or A
        else if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        }
        // Right or D
        else if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        }
    }

    // ===============================================
    // GAME OVER BUTTONS (ADDED BELOW)
    // ===============================================
    private void showGameOverButtons() {
        // Prevent duplicate panels
        for (Component comp : getComponents()) {
            if (comp instanceof JPanel && "overlay".equals(comp.getName())) {
                return;
            }
        }

        JPanel overlay = new JPanel();
        overlay.setName("overlay");
        overlay.setOpaque(true);
        overlay.setBackground(new Color(0, 0, 0, 150));
        overlay.setLayout(new GridBagLayout());

        JButton restartBtn = new JButton("Restart");
        JButton menuBtn = new JButton("Back to Menu");

        restartBtn.addActionListener(e -> restartGame());
        menuBtn.addActionListener(e -> returnToMenu());

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(restartBtn);
        buttons.add(Box.createHorizontalStrut(20));
        buttons.add(menuBtn);

        overlay.add(buttons);
        setLayout(new BorderLayout());
        add(overlay, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void restartGame() {
        removeAll();
        revalidate();
        repaint();

        // Reset variables
        snakeBody.clear();
        velocityX = 0;
        velocityY = 0;
        moves = 0;
        startTime = System.currentTimeMillis();
        gameOver = false;

        snakeHead = new Tile(random.nextInt((panelWidth / blockSize) - 10 + 1), random.nextInt((panelHeight / blockSize) - 10 + 1));
        placeFood();

        gameLoop.start();
        requestFocusInWindow();
        generateTimeModeValues();
    }

    private void returnToMenu() {
        if (frame != null) {
            frame.getContentPane().removeAll();
            frame.add(new IntroScreenUI(frame));
            frame.revalidate();
            frame.repaint();
        }
    }
}
