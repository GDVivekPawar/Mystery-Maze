import java.util.*;

public class LevelGenerator {

    int[][] maze;
    int TileWidth,TileHeight;
    Random random = new Random();


    LevelGenerator(int width, int height){
        TileWidth = width;
        TileHeight = height;
        maze = new int[TileWidth][TileHeight];
        generateMaze();
    }

    public void generateMaze(){
        for (int x = 0; x < TileWidth; x++) {
            for (int y = 0; y < TileHeight; y++) {
                maze[x][y] = 1; // Initialize all cells as 2D walls
            }
        }

        for (int x = 0; x < TileWidth; x++) {
            maze[x][TileHeight - 2] = 0;
            maze[x][TileHeight - 1] = 1;
        }
        for (int y = 1; y < TileHeight-1; y++) {
            maze[0][y] = 1;
            maze[TileWidth - 2][y] = 0;
            maze[TileWidth - 1][y] = 1;
        }

        carvePath(1, 1);

        // Place exit door
        maze[TileWidth - 2][TileHeight - 2] = 2; // 2 represents an exit door

        // Place hidden treasures
        int treasures = 3;
        for (int i = 0; i < treasures; i++) {
            int tx = random.nextInt(TileWidth - 1) + 1;
            int ty = random.nextInt(TileHeight - 1) + 1;
            if(maze[tx][ty] == 0){
                maze[tx][ty] = 3; // 3 represents a hidden treasure
            }
        }

        // Place spike obstacles
        int spikes = 5;
        for (int i = 0; i < spikes; i++) {
            int sx = random.nextInt(TileWidth - 1) + 1;
            int sy = random.nextInt(TileHeight - 1) + 1;
            if (maze[sx][sy] == 0) {
                maze[sx][sy] = 4; // 4 represents a spike obstacle
            }
        }

        // Place coins
        int coins = 30;
        for (int i = 0; i < coins; i++) {
            int cx = random.nextInt(TileWidth - 1) + 1;
            int cy = random.nextInt(TileHeight - 1) + 1;
            if (maze[cx][cy] == 0) {
                maze[cx][cy] = 6; // 6 represents a coin
                System.out.println("Coin placed at "+cx+", "+cy);
            }
        }

        add3DWalls();
    }

    private void carvePath(int x, int y) {
        
        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{x, y});
        maze[x][y] = 0; // Mark as path

        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int cx = current[0];
            int cy = current[1];

            // Randomly shuffle directions
            int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
            Collections.shuffle(Arrays.asList(directions));

            for (int[] direction : directions) {
                int nx = cx + direction[0] * 2;
                int ny = cy + direction[1] * 2;
                if (nx > 0 && nx < TileWidth - 1 && ny > 0 && ny < TileHeight - 1 && maze[nx][ny] == 1) {
                    maze[nx][ny] = 0;
                    maze[cx + direction[0]][cy + direction[1]] = 0;
                    stack.push(new int[]{nx, ny});
                }
            }
        }

        // Ensure goal is reachable
        maze[TileWidth - 2][TileHeight - 2] = 2; // Set exit tile
    }

    public void add3DWalls(){
        for(int x = 0; x < TileWidth; x++){
            for(int y = 0; y < TileHeight - 1; y++){
                if(maze[x][y] == 1 && ( maze[x][y + 1] == 0 || maze[x][y+1] == 6)){
                    maze[x][y] = 5;
                }
            }
        }
    }

    public enum Direction{
        NORTH(0, -1), SOUTH(0, 1), EAST(1, 0), WEST(-1, 0);

        int dx;
        int dy;

        Direction(int x, int y) {
            dx = x;
            dy = y;
        }

    }
}
