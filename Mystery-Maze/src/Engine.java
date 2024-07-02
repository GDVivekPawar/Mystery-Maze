import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.List;
import javax.swing.Timer;

public class Engine extends JPanel implements ActionListener, KeyListener, MouseListener, MouseMotionListener {
    
    Color startScreenBG = new Color(255, 255, 153);
    JButton startButton;
    
    Player player;
    Image HeroImg;
    List<Bomb> bombs;
    Image bombImg;
    Image bombFlashImg;
    List<Rectangle> Walls;
    Point touchPoint;
    long lastTimeTap;

    // LevelData
    LevelGenerator lvl;
    Image Tile1;
    Image Tile2;
    Image Tile3;
    Image exitImage;
    Image treasureImage;
    Image spikeImage;

    int TileSize = 32;
    int mazeWidth = 24;
    int mazeHeight = 24;

    boolean gameover = false;
    String message = "";

    Enemy enemy;
    Image enemyImage;

    Timer timer;

    JFrame jframe;
    CardLayout cardL;
    JPanel cardP;
    JLabel timerLabel;
    JLabel scoreLabel;
    int gameTime = 0;
    int score = 0;

    JLabel endScreenTitle;
    JLabel endScreenMessage;
    JLabel endScreenTimer;
    JLabel endScreenScore;

    public Engine(JFrame frame, JPanel cardJPanel, CardLayout cardLayout) {

        jframe = frame;
        cardP = cardJPanel;
        cardL = cardLayout;
        setBackground(startScreenBG);

        startButton = new JButton("START GAME");
        startButton.setFont(new Font("Arial", Font.BOLD,24));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(Color.YELLOW);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        
        JLabel mainChar = new JLabel(new ImageIcon("./V01_MainCharacter.png"));
        add(mainChar, gbc);

        JLabel title = new JLabel("Mystery Maze");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.yellow);
        gbc.gridy++;
        add(title, gbc);

        JLabel bomb = new JLabel(new ImageIcon("./V01_Bomb.png"));
        gbc.gridy++;
        add(bomb, gbc);

        gbc.gridy++;
        add(startButton, gbc);

        ImageIcon treasure = new ImageIcon("./V01_Treasure.png");
        startButton.setIcon(treasure);

        startButton.addActionListener(this);

        player = new Player();
        lvl = new LevelGenerator(mazeWidth, mazeHeight);
        bombs = new ArrayList<>();
        Walls = new ArrayList<>();
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                touchPoint = e.getPoint();
            }

            public void mouseReleased(MouseEvent e) {
                touchPoint = null;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTimeTap < 300) { // Double tap detected
                    deployBomb();
                }
                lastTimeTap = currentTime;
            }
        });
        addMouseMotionListener(this);

        setPreferredSize(new Dimension(mazeWidth * TileSize, mazeHeight * TileSize));
        
        LoadImages();


        JPanel hudPanel = new JPanel();
        timerLabel = new JLabel("Time: 0");
        scoreLabel = new JLabel("Score: 0");
        hudPanel.add(timerLabel);
        hudPanel.add(scoreLabel);
        setLayout(new BorderLayout());
        add(hudPanel, BorderLayout.NORTH);

        Random random = new Random();
        int enemyX, enemyY;
        do {
            enemyX = random.nextInt(mazeWidth - 2) + 1;
            enemyY = random.nextInt(mazeHeight - 2) + 1;
        } while (lvl.maze[enemyX][enemyY] != 0 || (enemyX == player.PosX && enemyY == player.PosY));

        enemy = new Enemy(lvl.maze,enemyX, enemyY);
        timer = new Timer(100,this);
        timer.start();

        timer = new Timer(1000, e -> {
            if (!gameover) {
                gameTime++;
                timerLabel.setText("Time: " + gameTime);
                repaint();
            }
        });
    }

    public void startGame() {
        gameover = false;
        gameTime = 0;
        score = 0;
        timerLabel.setText("Time: 0");
        scoreLabel.setText("Score: 0");
        player = new Player();
        lvl = new LevelGenerator(mazeWidth, mazeHeight);
        bombs.clear();
        Walls.clear();
        LoadImages();

        Random random = new Random();
        int enemyX, enemyY;
        do {
            enemyX = random.nextInt(mazeWidth - 2) + 1;
            enemyY = random.nextInt(mazeHeight - 2) + 1;
        } while (lvl.maze[enemyX][enemyY] != 0);

        enemy = new Enemy(lvl.maze, enemyX, enemyY);
        timer.start();
        requestFocus();
    }




    public void LoadImages() {
        try {
            HeroImg = loadImage("./V01_MainCharacter.png");
            Tile1 = loadImage("./V01_Tile1.png");
            Tile2 = loadImage("./V01_Tile2.png");
            Tile3 = loadImage("./V01_Tile3.png");
            bombImg = loadImage("./V01_Bomb.png");
            bombFlashImg = loadImage("./V01_Bomb_Flash.png");
            exitImage = loadImage("./V01_Door.png");
            treasureImage = loadImage("./V01_Treasure.png");
            enemyImage = loadImage("./V01_Enemy.png");
            spikeImage = loadImage("./V01_Obstacle.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Image loadImage(String path) throws IOException {
        Image image = ImageIO.read(getClass().getResource(path));
        if (image == null) {
            throw new IOException("Failed to load image: " + path);
        }
        return image;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawLevel(g);
        g.drawImage(enemyImage, enemy.x * TileSize, enemy.y * TileSize, TileSize, TileSize, null);
        g.drawImage(HeroImg, player.PosX * TileSize, player.PosY * TileSize, player.HeroWidth, player.HeroHeight, null);

        if (gameover) {
            g.setColor(Color.BLACK);
            g.drawString(message, getWidth() / 2 - 50, getHeight() / 2);
        }

        for (Bomb bomb : bombs) {
            if (bomb.isExploded()) {
                long elapsedTime = System.currentTimeMillis() - bomb.getExplodeTime();
                if (elapsedTime > Bomb.getExplosionDuration()) {
                    bombs.remove(bomb);
                    continue;
                }
                g.drawImage(bombImg, bomb.BPosX, bomb.BPosY, 32, 32, null);
                List<Rectangle> explosionBounds = bomb.getExplosionBounds(Walls);
                for (Rectangle bound : explosionBounds) {
                    g.drawImage(bombFlashImg, bound.x, bound.y, bound.width, bound.height, null);
                }
            } else {
                g.drawImage(bombImg, bomb.BPosX, bomb.BPosY, 32, 32, null);
            }
        }
    }

    public void drawLevel(Graphics g) {
        for (int x = 0; x < mazeWidth; x++) {
            for (int y = 0; y < mazeHeight; y++) {
                switch (lvl.maze[x][y]) {
                    case 0:
                        g.drawImage(Tile3, x * TileSize, y * TileSize, TileSize, TileSize, null);
                        break;
                    case 1:
                        g.drawImage(Tile1, x * TileSize, y * TileSize, TileSize, TileSize, null);
                        break;
                    case 2:
                        g.drawImage(exitImage, x * TileSize, y * TileSize, TileSize, TileSize, null);
                        break;
                    case 3:
                        g.drawImage(treasureImage, x * TileSize, y * TileSize, TileSize, TileSize, null);
                        break;
                    case 4:
                        g.drawImage(spikeImage, x * TileSize, y * TileSize, TileSize, TileSize, null);
                        break;
                    case 5:
                        g.drawImage(Tile2, x * TileSize, y * TileSize, TileSize, TileSize, null);
                        g.drawImage(Tile3, x * TileSize, (y + 1) * TileSize, TileSize, TileSize, null);
                        break;
                    default:
                        g.drawImage(Tile3, x * TileSize, y * TileSize, TileSize, TileSize, null);
                        break;
                }
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            movePlayer(0, -1);
        }
        if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
            movePlayer(0, 1);
        }
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            movePlayer(-1, 0);
        }
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            movePlayer(1, 0);
        }
        if (code == KeyEvent.VK_SPACE) {
            deployBomb();
        }
    }

    void movePlayer(int dx, int dy) {
        if (gameover) return;

        int newX = player.PosX + dx;
        int newY = player.PosY + dy;

        if (newX >= 0 && newX < mazeWidth && newY >= 0 && newY < mazeHeight && (lvl.maze[newX][newY] == 0 || lvl.maze[newX][newY] == 2 || lvl.maze[newX][newY] == 3 || lvl.maze[newX][newY] == 4)) {
            player.PosX = newX;
            player.PosY = newY;

            if (lvl.maze[player.PosX][player.PosY] == 2) {
                message = "You found the exit!";
                gameover = true;
                timer.stop();
                gameover = false;
                showEndScreen();
            } else if (lvl.maze[player.PosX][player.PosY] == 3) {
                message = "You found a hidden treasure!";
            } else if (lvl.maze[player.PosX][player.PosY] == 4) {
                message = "You hit a spike!";
                gameover = true;
                timer.stop();
                showEndScreen();
            }else if(enemy.isCollidingWithPlayer()){
                message = "You were caught by enemy!";
                gameover = true;
                timer.stop();
                showEndScreen();
            }
        }

        repaint();
    }

    private void deployBomb() {
        if (!gameover) {
            Bomb bomb = new Bomb(player.PosX, player.PosY);
            bombs.add(bomb);
            repaint();
        }
    }


    public void actionPerformed(ActionEvent e) {
        if (gameover) return;
        enemy.setPlayerPosition(player.PosX, player.PosY);

        if (enemy.isCollidingWithPlayer()) {
            gameover = true;
            message = "You were caught by the enemy!";
            timer.stop();
            showEndScreen();
        }

        repaint();
    }


    public void setEndScreenComponents(JLabel title, JLabel message, JLabel score, JLabel time){
        endScreenTitle = title;
        endScreenMessage = message;
        endScreenScore = score;
        endScreenTimer = time;
    }
    
    public void showEndScreen(){
        endScreenTitle.setText(gameover?"YOU LOSE": "YOU WIN");
        endScreenMessage.setText(message);
        cardL.show(cardP, "EndScreen");
        String scoreText = "Your Score: " + score;
        String timetext = "Your Time: " + gameTime;
        endScreenScore.setText(scoreText);
        endScreenTimer.setText(timetext);
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
