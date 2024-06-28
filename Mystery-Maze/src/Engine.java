import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class Engine extends JPanel implements ActionListener, KeyListener, MouseListener, MouseMotionListener {

    int boardWitdh = 768;
    int boardHeight = 768;
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
    Image Tile1;
    Image Tile2;
    Image Tile3;
    int TileWidth = 32;
    int TileHeight = 32;
    int x_Coord = 0;
    int y_Coord = 0;

    int[][] lvl1 = {
        {1,2,2,2,2,2,2,2,2,2,1,1,1,1,1,2,2,2,2,2,2,2,2,1},
        {1,3,3,3,3,3,3,3,3,3,1,1,1,1,1,3,3,3,3,3,3,3,3,1},
        {1,3,3,3,3,3,3,3,3,3,1,1,1,1,1,3,3,3,3,3,3,3,3,1},
        {1,3,3,3,3,3,3,3,3,3,1,1,1,1,1,3,3,3,3,3,3,3,3,1},
        {1,3,3,3,3,3,3,3,3,3,2,2,2,2,2,3,3,3,3,3,3,3,3,1},
        {1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1},
        {1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1},
        {1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1},
        {1,1,1,1,1,1,1,1,1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1},
        {1,1,1,1,1,1,1,1,1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1},
        {1,1,1,1,1,1,1,1,1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1},
        {1,2,2,2,2,2,2,2,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1},
        {1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1},
        {1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1},
        {1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1},
        {1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1},
        {1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1},
        {1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1},
        {1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1},
        {1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1},
        {1,1,1,1,1,1,1,1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1},
        {1,1,1,1,1,1,1,1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1},
        {1,1,1,1,1,1,1,1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1},
        {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
    };

    Engine(){
        player = new Player(boardWitdh, boardHeight);
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
        setPreferredSize(new Dimension(boardWitdh, boardHeight));
        LoadImages();

    }

    public void LoadImages(){
        HeroImg = new ImageIcon(getClass().getResource("./V01_MainCharacter.png")).getImage();
        Tile1 = new ImageIcon(getClass().getResource("./V01_Tile1.png")).getImage();
        Tile2 = new ImageIcon(getClass().getResource("./V01_Tile2.png")).getImage();
        Tile3 = new ImageIcon(getClass().getResource("./V01_Tile3.png")).getImage();
        bombImg = new ImageIcon(getClass().getResource("./V01_Bomb.png")).getImage();
        bombFlashImg = new ImageIcon(getClass().getResource("./V01_Bomb_Flash.png")).getImage();
    }

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_UP || code == KeyEvent.VK_W)
        {
            player.moveUp(player.PosX, player.PosY, Walls);
        }
        if(code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S)
        {
            player.moveDown(player.PosX, player.PosY, Walls);
        }
        if(code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A)
        {
            player.moveLeft(player.PosX, player.PosY, Walls);
        }
        if(code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D)
        {
            player.moveRight(player.PosX, player.PosY, Walls);
        }
        if(code == KeyEvent.VK_SPACE)
        {
            deployBomb();
        }
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
                    player.moveRight(1, 0, Walls); // Move right
                } else {
                    player.moveLeft(-1, 0, Walls); // Move left
                }
            } else {
                // Vertical movement
                if (dy > 0) {
                    player.moveDown(0, 1, Walls); // Move down
                } else {
                    player.moveUp(0, -1, Walls); // Move up
                }
            }
        }
        touchPoint = newTouchPoint;
    }

    
    public void mouseMoved(MouseEvent e) {}

    public void deployBomb(){
        bombs.add(new Bomb(player.PosX,player.PosY));
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        
        for(int i = 0 ; i < 24; i++)
        {
            for(int j = 0; j < 24; j++)
            {
                if(lvl1[i][j] == 1)
                {
                    Walls.add(new Rectangle(x_Coord, y_Coord, TileWidth, TileHeight));
                    g.drawImage(Tile1,x_Coord,y_Coord,TileWidth,TileHeight,null);
                    x_Coord += 32;
                }
                if(lvl1[i][j] == 2)
                {
                    Walls.add(new Rectangle(x_Coord, y_Coord, TileWidth, TileHeight));
                    g.drawImage(Tile2,x_Coord,y_Coord,TileWidth,TileHeight,null);
                    x_Coord += 32;
                }
                if(lvl1[i][j] == 3)
                {
                    g.drawImage(Tile3,x_Coord,y_Coord,TileWidth,TileHeight,null);
                    x_Coord += 32;
                }                
            }
            x_Coord = 0;
            y_Coord += 32;
        }
        y_Coord = 0;
        g.drawImage(HeroImg,player.PosX,player.PosY,player.HeroWidth,player.HeroHeight,null);

        for(Bomb bomb : bombs){
            if(bomb.isExploded()){
                g.drawImage(bombFlashImg, bomb.BPosX, bomb.BPosY, 32, 32, null);
                List<Rectangle> explosionBounds = bomb.getExplosionBounds(Walls);
                for(Rectangle bound : explosionBounds){
                    g.drawImage(bombFlashImg, bound.x, bound.y, bound.width, bound.height, null);
                }
            }
            else{
                g.drawImage(bombImg, bomb.BPosX, bomb.BPosY, 32, 32, null);
            }
        }
    }

    public void actionPerformed(ActionEvent e){
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mouseClicked'");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mousePressed'");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mouseReleased'");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mouseEntered'");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mouseExited'");
    }
    
}
