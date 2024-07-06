import javax.swing.*;
import java.awt.*;

public class Game {
    public static void main(String[] args) throws Exception {
        int boardWidth = 768;
        int boardHeight = 768;

        JFrame frame = new JFrame("Mystery-Maze");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);

        Engine maze = new Engine(frame, cardPanel, cardLayout);
        maze.setPreferredSize(new Dimension(boardWidth, boardHeight));

        JPanel startScreen = createStartScreen(cardPanel, cardLayout, maze);
        JPanel endScreen = createEndScreen(cardPanel, cardLayout, maze);
        cardPanel.add(startScreen, "StartScreen");
        cardPanel.add(maze, "GamePanel");
        cardPanel.add(endScreen, "EndScreen");

        frame.add(cardPanel);
        frame.pack();
        frame.setVisible(true);

        cardLayout.show(cardPanel, "StartScreen");
    }

    private static JPanel createStartScreen(JPanel cardPanel, CardLayout cardLayout, Engine maze) {
        JPanel startScreen = new JPanel(null);
        startScreen.setBackground(new Color(255, 255, 153));

        int width = 768;
        int height = 768;
        int topMargin = height / 4;

        // Load and scale images
        ImageIcon mainCharIcon = new ImageIcon(new ImageIcon(Game.class.getResource("/V01_MainCharacter.png")).getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        ImageIcon bombIcon = new ImageIcon(new ImageIcon(Game.class.getResource("/V01_Bomb.png")).getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        ImageIcon enemyIcon = new ImageIcon(new ImageIcon(Game.class.getResource("/V01_Enemy.png")).getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        ImageIcon treasureIcon = new ImageIcon(new ImageIcon(Game.class.getResource("/V01_Treasure.png")).getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));

        // Main character label
        JLabel mainChar = new JLabel(mainCharIcon);
        mainChar.setBounds(width / 4 - 10, topMargin - 16, 32, 32);  
        startScreen.add(mainChar);

        // Bomb label
        JLabel bomb = new JLabel(bombIcon);
        bomb.setBounds(width / 2 + 16, topMargin - 16, 32, 32);  
        startScreen.add(bomb);

        // Title label
        JLabel title = new JLabel("Mystery   Maze", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 45));
        title.setForeground(Color.BLACK);
        title.setBounds(width / 2 - 300, topMargin - 50, 600, 100);
        startScreen.add(title);

        // Enemy with bomb label
        JLabel enemyWithBomb = new JLabel(enemyIcon);
        enemyWithBomb.setBounds(width - (width / 4) - 25, topMargin - 16, 32, 32);
        startScreen.add(enemyWithBomb);

        // Start button with treasure chest icon
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(Color.BLACK);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        startButton.setIcon(treasureIcon);
        startButton.setBounds(width / 2 - 200, height / 2, 400, 100);
        startButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "GamePanel");
            maze.startGame();
            maze.requestFocus();
        });
        startScreen.add(startButton);

        return startScreen;
    }

    private static JPanel createEndScreen(JPanel cardPanel, CardLayout cardLayout, Engine maze) {
        JPanel endScreen = new JPanel(null);
        endScreen.setBackground(new Color(255, 255, 153));

        int width = 768;
        int height = 768;
        int topMargin = height / 4;

        // Title label
        JLabel title = new JLabel("", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.BLACK);
        title.setBounds(width / 2 - 100, topMargin - 25, 200, 50);
        endScreen.add(title);

        // Message label
        JLabel messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setBounds(width / 2 - 200, topMargin + 25, 400, 50);
        endScreen.add(messageLabel);

        // Score Label
        JLabel ScoreLabel = new JLabel("", SwingConstants.CENTER);
        ScoreLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        ScoreLabel.setForeground(Color.BLACK);
        ScoreLabel.setBounds(width / 2 - 300, topMargin + 80, 200, 50);
        endScreen.add(ScoreLabel);

        // Time Label
        JLabel timeLabel = new JLabel("", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        timeLabel.setForeground(Color.BLACK);
        timeLabel.setBounds(width / 2 + 100, topMargin + 80, 150, 50);
        endScreen.add(timeLabel);

        // Retry button
        JButton nextLevelButton = new JButton("Retry");
        nextLevelButton.setFont(new Font("Arial", Font.BOLD, 24));
        nextLevelButton.setForeground(Color.WHITE);
        nextLevelButton.setBackground(Color.BLACK);
        nextLevelButton.setFocusPainted(false);
        nextLevelButton.setBounds(width / 2 - 100, height / 2, 200, 50);
        nextLevelButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "GamePanel");
            maze.startGame();
            maze.requestFocus();
        });
        endScreen.add(nextLevelButton);

        // Quit button
        JButton quitButton = new JButton("Quit");
        quitButton.setFont(new Font("Arial", Font.BOLD, 24));
        quitButton.setForeground(Color.WHITE);
        quitButton.setBackground(Color.BLACK);
        quitButton.setFocusPainted(false);
        quitButton.setBounds(width / 2 - 100, height / 2 + 75, 200, 50);
        quitButton.addActionListener(e -> System.exit(0));
        endScreen.add(quitButton);

        maze.setEndScreenComponents(title, messageLabel, ScoreLabel, timeLabel);

        return endScreen;
    }
}
