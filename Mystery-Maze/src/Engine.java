import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Engine extends JPanel implements ActionListener, KeyListener, MouseListener, MouseMotionListener {

    Timer timer;
    
    Player player;
    Image HeroImg;
    List<Bomb> bombs;
    Image bombImg;
    Image bombFlashImg;
    List<Rectangle> Walls;
    Point touchPoint;
    long lastTimeTap;

    //LevelData
    LevelGenerator lvl;
    Image Tile1;
    Image Tile2;
    Image Tile3;
    Image exitImage;
    Image treasureImage;
    Image spikeImage;

    int TileSize = 32;
    int x_Coord = 0;
    int y_Coord = 0;
    int mazeWidth = 24;
    int mazeHeight = 24;

    boolean gameover = false;
    String message = "";


    Engine(){
        player = new Player();
        lvl = new LevelGenerator(mazeWidth,mazeHeight);
        bombs = new ArrayList<>();
        Walls = new ArrayList<>();
        timer = new Timer(10,this);
        timer.start();
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

    }

    public void LoadImages(){
        try{
            HeroImg = loadImage("./V01_MainCharacter.png");
            Tile1 = loadImage("./V01_Tile1.png");
            Tile2 = loadImage("./V01_Tile2.png");
            Tile3 = loadImage("./V01_Tile3.png");
            bombImg = loadImage("./V01_Bomb.png");
            bombFlashImg = loadImage("./V01_Bomb_Flash.png");
            exitImage = loadImage("./V01_Door.png");
            treasureImage = loadImage("./V01_Treasure.png");
            spikeImage = loadImage("./V01_Obstacle.png");
        } catch (IOException e){
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

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        drawLevel(g);
        
        g.drawImage(HeroImg,player.PosX * TileSize, player.PosY * TileSize, player.HeroWidth, player.HeroHeight,null);

        if(gameover)
        {
            g.setColor(Color.BLACK);
            g.drawString(message, getWidth()/2 - 50, getHeight()/2);
        }

        Iterator<Bomb> iterator = bombs.iterator();
        while (iterator.hasNext()) {
            Bomb bomb = iterator.next();
            if (bomb.isExploded()) {
    
                long elapsedTime = System.currentTimeMillis() - bomb.getExplodeTime();
                if (elapsedTime > Bomb.getExplosionDuration()) {
                    iterator.remove();
                    continue;
                }
                // Draw the bomb image centered at the bomb position
                g.drawImage(bombImg, bomb.BPosX, bomb.BPosY, 32, 32, null);

                List<Rectangle> explosionBounds = bomb.getExplosionBounds(Walls);
                for (Rectangle bound : explosionBounds) {
                    g.drawImage(bombFlashImg, bound.x, bound.y, bound.width, bound.height, null);
                }

            } else {
                // Draw bomb
                g.drawImage(bombImg, bomb.BPosX, bomb.BPosY, 32, 32, null);
            }
        }
    }

    public void drawLevel(Graphics g)
    {
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
        if(code == KeyEvent.VK_UP || code == KeyEvent.VK_W)
        {
            movePlayer(0,-1);
        }
        if(code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S)
        {
            movePlayer(0, 1);
        }
        if(code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A)
        {
            movePlayer(- 1, 0);
        }
        if(code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D)
        {
            movePlayer(1, 0);
        }
        if(code == KeyEvent.VK_SPACE)
        {
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
            } else if (lvl.maze[player.PosX][player.PosY] == 3) {
                message = "You found a hidden treasure!";
            } else if (lvl.maze[player.PosX][player.PosY] == 4) {
                message = "You hit a spike!";
                gameover = true;
            }
        }

        repaint();
    }

    public void keyReleased(KeyEvent e){}
    public void keyTyped(KeyEvent e){}
    
    public void mouseDragged(MouseEvent e) {
        Point newTouchPoint = e.getPoint();
        if (touchPoint != null) {
            int dx = newTouchPoint.x - touchPoint.x;
            int dy = newTouchPoint.y - touchPoint.y;
            
            if (Math.abs(dx) > Math.abs(dy)) {
                // Horizontal movement
                if (dx > 0) {
                    movePlayer(1, 0); // Move right
                } else {
                    movePlayer(-1, 0); // Move left
                }
            } else {
                // Vertical movement
                if (dy > 0) {
                    movePlayer(0, 1); // Move down
                } else {
                    movePlayer(0, -1); // Move up
                }
            }
        }
        touchPoint = newTouchPoint;
    }

    
    public void mouseMoved(MouseEvent e) {}

    public void deployBomb(){
        bombs.add(new Bomb(player.PosX,player.PosY));
    }

    public void actionPerformed(ActionEvent e){
        repaint();
    }

    public void mouseClicked(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}
    
}
