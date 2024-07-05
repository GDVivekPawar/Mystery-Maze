import java.util.Random;
import java.util.List;

public class Enemy {
    public final int[][] maze;
    public final int tileSize;
    public final Random random;
    public int x, y;
    public AIState state;
    public int playerX, playerY;
    public int direction;
    public int sightRange;
    public long lastSawPlayerTime;
    public long loseSightTime = 3000;
    public int patrolRadius;
    private int startX, startY;
    private List<Bomb> bombs; // Reference to the list of bombs
    public boolean isDead;
    public long deathTime;
    public static long deathDisplayTime = 3000;
    public int deadX, deadY;

    private enum AIState { PATROLLING, CHASING }

    public Enemy(int[][] lvl, int startX, int startY, List<Bomb> bombs) {
        maze = lvl;
        tileSize = 32;
        random = new Random();
        x = startX;
        y = startY;
        this.startX = startX;
        this.startY = startY;
        state = AIState.PATROLLING;
        direction = random.nextInt(4);
        sightRange = 5;
        lastSawPlayerTime = System.currentTimeMillis();
        patrolRadius = 5;
        this.bombs = bombs; // Initialize the bombs reference
        isDead = false;
    }

    public void setPlayerPosition(int x, int y) {

        if(isDead){
            return;
        }


        playerX = x;
        playerY = y;

        if (canSeePlayer()) {
            state = AIState.CHASING;
            lastSawPlayerTime = System.currentTimeMillis();
        } else if (System.currentTimeMillis() - lastSawPlayerTime > loseSightTime) {
            state = AIState.PATROLLING;
        }

        if (state == AIState.CHASING) {
            chasePlayer();
        } else {
            patrol();
        }
    }

    public boolean canSeePlayer() {
        int dx = playerX - x;
        int dy = playerY - y;
        if (Math.abs(dx) <= sightRange && Math.abs(dy) <= sightRange) {
            // Simplified line of sight check
            for (int i = 1; i <= Math.max(Math.abs(dx), Math.abs(dy)); i++) {
                int checkX = x + (dx != 0 ? dx / Math.abs(dx) * i : 0);
                int checkY = y + (dy != 0 ? dy / Math.abs(dy) * i : 0);
                if (maze[checkX][checkY] != 0 || isBombAt(checkX, checkY)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private void chasePlayer() {
        if (playerX > x && canMoveTo(x + 1, y)) {
            x++;
        } else if (playerX < x && canMoveTo(x - 1, y)) {
            x--;
        } else if (playerY > y && canMoveTo(x, y + 1)) {
            y++;
        } else if (playerY < y && canMoveTo(x, y - 1)) {
            y--;
        }
    }

    public void patrol() {
        if (Math.abs(x - startX) > patrolRadius || Math.abs(y - startY) > patrolRadius) {
            direction = random.nextInt(4);
        }

        switch (direction) {
            case 0: // Up
                if (canMoveTo(x, y - 1)) {
                    y--;
                } else {
                    changeDirection();
                }
                break;
            case 1: // Down
                if (canMoveTo(x, y + 1)) {
                    y++;
                } else {
                    changeDirection();
                }
                break;
            case 2: // Left
                if (canMoveTo(x - 1, y)) {
                    x--;
                } else {
                    changeDirection();
                }
                break;
            case 3: // Right
                if (canMoveTo(x + 1, y)) {
                    x++;
                } else {
                    changeDirection();
                }
                break;
        }
    }

    private boolean canMoveTo(int x, int y) {
        return maze[x][y] == 0 && !isBombAt(x, y);
    }

    private boolean isBombAt(int x, int y) {
        for (Bomb bomb : bombs) {
            if (bomb.BPosX / tileSize == x && bomb.BPosY / tileSize == y) {
                return true;
            }
        }
        return false;
    }

    public void changeDirection() {
        direction = random.nextInt(4);
    }

    public boolean isCollidingWithPlayer() {
        return x == playerX && y == playerY;
    }

    public void die() {
        isDead = true;
        deathTime = System.currentTimeMillis();
        deadX = x;
        deadY = y;
    }

}
