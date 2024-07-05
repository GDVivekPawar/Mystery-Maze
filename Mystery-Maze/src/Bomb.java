import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

public class Bomb {
    int BPosX, BPosY;
    int size = 32;
    boolean exploded;
    long explodeTime;
    static long explosionDuration = 2000;
    private static final int RADIUS = 2;

    public Bomb(int x, int y) {
        BPosX = x * size;
        BPosY = y * size;
        exploded = false;

        new Timer((int) explosionDuration, e -> {
            exploded = true;
            explodeTime = System.currentTimeMillis();
            ((Timer) e.getSource()).stop();
        }).start();
    }

    public Rectangle getBounds() {
        return new Rectangle(BPosX, BPosY, size, size);
    }

    public boolean isExploded() {
        return exploded;
    }

    public long getExplodeTime() {
        return explodeTime;
    }

    public static long getExplosionDuration() {
        return explosionDuration;
    }

    public List<Rectangle> getExplosionBounds(int[][] maze) {
        List<Rectangle> explosionBounds = new ArrayList<>();
        int tileSize = 32;

        // Center explosion
        explosionBounds.add(new Rectangle(BPosX, BPosY, tileSize, tileSize));

        // Check in four directions: up, down, left, right
        addExplosionBound(explosionBounds, maze, BPosX, BPosY - tileSize, RADIUS, 0, -1);
        addExplosionBound(explosionBounds, maze, BPosX, BPosY + tileSize, RADIUS, 0, 1);
        addExplosionBound(explosionBounds, maze, BPosX - tileSize, BPosY, RADIUS, -1, 0);
        addExplosionBound(explosionBounds, maze, BPosX + tileSize, BPosY, RADIUS, 1, 0);

        return explosionBounds;
    }

    private void addExplosionBound(List<Rectangle> explosionBounds, int[][] maze, int startX, int startY, int radius, int dx, int dy) {
        for (int i = 0; i < radius; i++) {
            int tileX = (startX / size) + i * dx;
            int tileY = (startY / size) + i * dy;

            if (tileX < 0 || tileX >= maze.length || tileY < 0 || tileY >= maze[0].length) {
                break;
            }

            Rectangle bound = new Rectangle(tileX * size, tileY * size, size, size);
            if (maze[tileX][tileY] == 1 || maze[tileX][tileY] == 5) { // Assuming 1 represents walls
                break;
            }
            explosionBounds.add(bound);
        }
    }

    public boolean isWithinRange(int x, int y, int[][] maze) {
        List<Rectangle> explosionBounds = getExplosionBounds(maze);
        Rectangle position = new Rectangle(x * size, y * size, size, size);
        for (Rectangle bound : explosionBounds) {
            if (bound.intersects(position)) {
                return true;
            }
        }
        return false;
    }
}
