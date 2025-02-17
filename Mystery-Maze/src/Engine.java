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
    int noOfBombs = 3;
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
    Image coinImage;

    int TileSize = 32;
    int mazeWidth = 24;
    int mazeHeight = 24;

    boolean gameover = false;
    String message = "";

    Enemy enemy;
    Image enemyImage;
    Image enemyDeadImage;

    boolean haskey;
    Image key;
    int keyX, keyY;

    Timer timer;

    JFrame jframe;
    CardLayout cardL;
    JPanel cardP;
    JLabel timerLabel;
    JLabel scoreLabel;
    JLabel BombsRemaining;
    int gameTime = 0;
    int score = 0;

    JLabel endScreenTitle;
    JLabel endScreenMessage;
    JLabel endScreenTimer;
    JLabel endScreenScore;

    Sound GameBGM;
    Sound GameWinSound;
    Sound GameLoseSound;
    Sound CoinPickUp;
    Sound BombSound;

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

        GameBGM = new Sound("./sounds/GameBGM.wav");
        GameWinSound = new Sound("./sounds/GameWinSound.wav");
        GameLoseSound = new Sound("./sounds/GameLoseSound.wav");
        CoinPickUp = new Sound("./sounds/CoinPickUp.wav");
        BombSound = new Sound("./sounds/BombSound.wav");

        GameBGM.loop();

        startButton.setIcon(treasure);

        startButton.addActionListener(this);

        player = new Player();
        lvl = new LevelGenerator(mazeWidth, mazeHeight);
        bombs = new ArrayList<>(3);
        Walls = new ArrayList<>();
        haskey = false;
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
        BombsRemaining = new JLabel("Bombs: 3");
        hudPanel.add(timerLabel);
        hudPanel.add(scoreLabel);
        hudPanel.add(BombsRemaining);
        setLayout(new BorderLayout());
        add(hudPanel, BorderLayout.NORTH);

        Random random = new Random();
        int enemyX, enemyY;
        do {
            enemyX = random.nextInt(mazeWidth - 2) + 1;
            enemyY = random.nextInt(mazeHeight - 2) + 1;
        } while (lvl.maze[enemyX][enemyY] != 0 || (enemyX == player.PosX && enemyY == player.PosY));
        do {
            keyX = random.nextInt(mazeWidth - 2) + 1;
            keyY = random.nextInt(mazeHeight - 2) + 1;
        } while (lvl.maze[keyX][keyY] != 0 || (keyX == player.PosX && keyY == player.PosY));

        enemy = new Enemy(lvl.maze,enemyX, enemyY, bombs);
        timer = new Timer(100,this);
        timer.start();

        timer = new Timer(1000, e -> {
            if (!gameover) {
                gameTime++;
                timerLabel.setText("Time: " + gameTime);
                repaint();
            }
        });

        long currentTime = System.currentTimeMillis();

    // Update enemy position and state
    if (enemy != null && !enemy.isDead) {
        enemy.setPlayerPosition(player.PosX, player.PosY);
    }

    // Check if the dead image should be removed
    if (enemy != null && enemy.isDead && currentTime - enemy.deathTime > Enemy.deathDisplayTime) {
        enemy = null; // Remove the enemy
    }

    Timer GiftBombTimer = new Timer(20000, new ActionListener() {
        public void actionPerformed(ActionEvent e){
            if(noOfBombs < 3){
                noOfBombs++;
                BombsRemaining.setText("Bombs: "+ noOfBombs);
            }
        }
    });
    GiftBombTimer.start();
    }

    public void startGame() {
        gameover = false;
        gameTime = 0;
        score = 0;
        timerLabel.setText("Time: 0");
        scoreLabel.setText("Score: 0");
        BombsRemaining.setText("Bombs: 3");
        player = new Player();
        lvl = new LevelGenerator(mazeWidth, mazeHeight);
        bombs.clear();
        Walls.clear();
        LoadImages();
        haskey = false;
        GameBGM.loop();

        Random random = new Random();
        int enemyX, enemyY;
        do {
            enemyX = random.nextInt(mazeWidth - 2) + 1;
            enemyY = random.nextInt(mazeHeight - 2) + 1;
        } while (lvl.maze[enemyX][enemyY] != 0);

        enemy = new Enemy(lvl.maze, enemyX, enemyY, bombs);
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
            enemyDeadImage = loadImage("./V01_Enemy_dead.png");
            coinImage = loadImage("./V01_Coin.png");
            key = loadImage("./V01_Key.png");
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
        if (enemy != null) {
            if (enemy.isDead) {
                g.drawImage(enemyDeadImage, enemy.deadX * TileSize, enemy.deadY * TileSize, 32, 32,null);
            } else {
                g.drawImage(enemyImage, enemy.x * TileSize, enemy.y * TileSize, 32, 32, null);
            }
        }
        g.drawImage(HeroImg, player.PosX * TileSize, player.PosY * TileSize, player.HeroWidth, player.HeroHeight, null);

        if (gameover) {
            g.setColor(Color.BLACK);
            g.drawString(message, getWidth() / 2 - 50, getHeight() / 2);
        }

        Iterator<Bomb> iterator = bombs.iterator();
        while (iterator.hasNext()) {
            Bomb bomb = iterator.next();
            if (bomb.isExploded()) {
                if(enemy != null && bomb.isWithinRange(enemy.x, enemy.y, lvl.maze)){
                    enemy.die();
                    score += 50;
                    scoreLabel.setText("Score = " + score);
                }
                long elapsedTime = System.currentTimeMillis() - bomb.getExplodeTime();
                if (elapsedTime > Bomb.getExplosionDuration()) {
                    iterator.remove();
                    continue;
                }
                g.drawImage(bombImg, bomb.BPosX, bomb.BPosY, 32, 32, null);
                List<Rectangle> explosionBounds = bomb.getExplosionBounds(lvl.maze);
                for (Rectangle bound : explosionBounds) {
                    g.drawImage(bombFlashImg, bound.x, bound.y, bound.width, bound.height, null);
                    BombSound.play();
                }

                // Check for collision with player, enemy, and spikes
                for (Rectangle bound : explosionBounds) {
                    if (bound.intersects(new Rectangle(player.PosX * TileSize, player.PosY * TileSize, TileSize, TileSize))) {
                        gameover = true;
                        message = "You were caught in the explosion!";
                        timer.stop();
                        showEndScreen();
                    }
                    if (enemy != null && bound.intersects(new Rectangle(enemy.x * TileSize, enemy.y * TileSize, TileSize, TileSize))) {
                        enemy = null; // Enemy destroyed
                    }
                    // Check and destroy spikes if they are within explosion bounds
                    for (int x = 0; x < mazeWidth; x++) {
                        for (int y = 0; y < mazeHeight; y++) {
                            if (lvl.maze[x][y] == 4 && bound.intersects(new Rectangle(x * TileSize, y * TileSize, TileSize, TileSize))) {
                                lvl.maze[x][y] = 0; // Destroy spike
                            }
                        }
                    }
                }
            } else {
                g.drawImage(bombImg, bomb.BPosX, bomb.BPosY, 32, 32, null);
            }
        }
    }

    public void drawLevel(Graphics g) {
        for (int x = 0; x < lvl.TileWidth; x++) {
            for (int y = 0; y < lvl.TileHeight; y++) {
                if (lvl.maze[x][y] == 0) {
                    g.drawImage(Tile3, x * TileSize, y * TileSize, TileSize, TileSize, null);
                }
                if (lvl.maze[x][y] == 1) {
                    g.drawImage(Tile1, x * TileSize, y * TileSize, TileSize, TileSize, null);
                }
                if (lvl.maze[x][y] == 2) {
                    g.drawImage(exitImage, x * TileSize, y * TileSize, TileSize, TileSize, null);
                }
                if (lvl.maze[x][y] == 3) {
                    g.drawImage(treasureImage, x * TileSize, y * TileSize, TileSize, TileSize, null);
                }
                if (lvl.maze[x][y] == 4) {
                    g.drawImage(spikeImage, x * TileSize, y * TileSize, TileSize, TileSize, null);
                }
                if (lvl.maze[x][y] == 5) {
                    g.drawImage(Tile2, x * TileSize, y * TileSize, TileSize, TileSize, null);
                }
            }
        }
    
        // Draw coins after all other tiles
        for (int x = 0; x < lvl.TileWidth; x++) {
            for (int y = 0; y < lvl.TileHeight; y++) {
                if (lvl.maze[x][y] == 6) { // Draw coin
                    g.drawImage(coinImage, x * TileSize, y * TileSize, TileSize, TileSize, null);
                }
            }
        }

        if(!haskey){
            g.drawImage(key, keyX * TileSize, keyY * TileSize, TileSize, TileSize, null);
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

        if (newX >= 0 && newX < mazeWidth && newY >= 0 && newY < mazeHeight && (lvl.maze[newX][newY] == 0 || lvl.maze[newX][newY] == 2 || lvl.maze[newX][newY] == 3 || lvl.maze[newX][newY] == 4 || lvl.maze[newX][newY] == 6) && !isBombAt(newX, newY)) {
            if (newX == keyX && newY == keyY) {
                CoinPickUp.play();
                haskey = true;
                lvl.maze[keyX][keyY] = 0; // remove the key from the map
            }
            player.PosX = newX;
            player.PosY = newY;


            if (lvl.maze[player.PosX][player.PosY] == 2 && haskey) {
                message = "You found the exit!";
                gameover = true;
                timer.stop();
                gameover = false;
                showEndScreen();
            } else if (lvl.maze[player.PosX][player.PosY] == 3) {
                CoinPickUp.play();
                score += 100;
                scoreLabel.setText("Score: " + score);
                lvl.maze[newX][newY] = 0;
                message = "You found a hidden treasure!";
            } else if (lvl.maze[player.PosX][player.PosY] == 4) {
                message = "You hit a spike!";
                gameover = true;
                timer.stop();
                showEndScreen();
            }else if (lvl.maze[player.PosX][player.PosY] == 6) {
                CoinPickUp.play();
                score += 10; // Increment score by 10 for each coin collected
                scoreLabel.setText("Score: " + score); // Update HUD
                lvl.maze[player.PosX][player.PosY] = 0; // Set to grass tile after collection
            }else if(enemy != null){
                if(enemy.isCollidingWithPlayer()){
                message = "You were caught by enemy!";
                gameover = true;
                timer.stop();
                showEndScreen();
                }
            }
        }

        repaint();
    }

    private boolean isBombAt(int x, int y){
        for(Bomb bomb : bombs){
            if(bomb.BPosX/32 == x && bomb.BPosY/32 == y){
                return true;
            }
        }
        return false;
    }

    private void deployBomb() {
        if (!gameover && noOfBombs > 0) {
            Bomb bomb = new Bomb(player.PosX, player.PosY);
            bombs.add(bomb);
            noOfBombs--;
            BombsRemaining.setText("Bombs: " + noOfBombs);
            repaint();
        }
    }


    public void actionPerformed(ActionEvent e) {
        if (gameover) return;
        if(enemy != null){
        enemy.setPlayerPosition(player.PosX, player.PosY);
        }

        if (enemy != null){ 
                if(enemy.isCollidingWithPlayer()) {
                gameover = true;
                message = "You were caught by the enemy!";
                timer.stop();
                showEndScreen();
            }

        }


    }


    public void setEndScreenComponents(JLabel title, JLabel message, JLabel score, JLabel time){
        endScreenTitle = title;
        endScreenMessage = message;
        endScreenScore = score;
        endScreenTimer = time;
    }
    
    public void showEndScreen(){
        if(gameover){
            GameLoseSound.play();
        }
        else{
            GameWinSound.play();
        }
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
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
}
