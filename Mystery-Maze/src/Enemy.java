import java.util.Random;

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

    private enum AIState { PATROLLING, CHASING }

    public Enemy(int[][] lvl, int startX, int startY) {
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
    }

    public void setPlayerPosition(int x, int y) {
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
                if (maze[checkX][checkY] != 0) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private void chasePlayer() {
        if (playerX > x && maze[x + 1][y] == 0) {
            x++;
        } else if (playerX < x && maze[x - 1][y] == 0) {
            x--;
        } else if (playerY > y && maze[x][y + 1] == 0) {
            y++;
        } else if (playerY < y && maze[x][y - 1] == 0) {
            y--;
        }
    }

    public void patrol() {
        if (Math.abs(x - startX) > patrolRadius || Math.abs(y - startY) > patrolRadius) {
            direction = random.nextInt(4);
        }

        switch (direction) {
            case 0: // Up
                if (maze[x][y - 1] == 0) {
                    y--;
                } else {
                    changeDirection();
                }
                break;
            case 1: // Down
                if (maze[x][y + 1] == 0) {
                    y++;
                } else {
                    changeDirection();
                }
                break;
            case 2: // Left
                if (maze[x - 1][y] == 0) {
                    x--;
                } else {
                    changeDirection();
                }
                break;
            case 3: // Right
                if (maze[x + 1][y] == 0) {
                    x++;
                } else {
                    changeDirection();
                }
                break;
        }
    }

    public void changeDirection() {
        direction = random.nextInt(4);
    }

    public boolean isCollidingWithPlayer() {
        return x == playerX && y == playerY;
    }
}
