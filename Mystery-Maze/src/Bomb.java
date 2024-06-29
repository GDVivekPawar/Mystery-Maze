import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Bomb {
    int BPosX,BPosY;
    int sixe = 32;
    boolean exploded;
    long explodeTime;
    static long explosionDuration = 2000;

    public Bomb(int x, int y){
        BPosX = x;
        BPosY = y;
        exploded = false;

        new Timer((int)explosionDuration, new ActionListener() {
            public void actionPerformed(ActionEvent e){
                exploded = true;
                explodeTime = System.currentTimeMillis();
                ((Timer) e.getSource()).stop();
            }
        }).start();
    }

    public Rectangle getBounds(){
        return new Rectangle(BPosX, BPosY, 32, 32);
    }

    public void explode(){
        exploded = true;
    }

    public boolean isExploded(){
        return exploded;
    }

    public long getExplodeTime() {
        return explodeTime;
    }

    public static long getExplosionDuration() {
        return explosionDuration;
    }

    public List<Rectangle> getExplosionBounds(List<Rectangle> walls){
        List<Rectangle> explosionBounds = new ArrayList<>();

        int radius = 64;

        Rectangle up = new Rectangle(BPosX, BPosY - radius/2, 32, radius);
        Rectangle down = new Rectangle(BPosX, BPosY, 32, radius);
        Rectangle left = new Rectangle(BPosX - radius/2, BPosY, radius, 32);
        Rectangle right = new Rectangle(BPosX, BPosY, radius, 32);

        if (!isBlocked(up, walls)) explosionBounds.add(up);
        if (!isBlocked(down, walls)) explosionBounds.add(down);
        if (!isBlocked(left, walls)) explosionBounds.add(left);
        if (!isBlocked(right, walls)) explosionBounds.add(right);

        return explosionBounds;
    }

    public boolean isBlocked(Rectangle explosionBound, List<Rectangle> walls){
        for (Rectangle wall : walls) {
            if (explosionBound.intersects(wall)) {
                return true;
            }
        }
        return false;
    }
}
