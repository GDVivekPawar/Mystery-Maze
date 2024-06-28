import javax.swing.*;

public class Game {
    public static void main(String[] args) throws Exception {
        int boardWidth = 768;
        int boardHeight = 768;

        JFrame frame = new JFrame("Mystery-Maze");
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Engine maze = new Engine();
        frame.add(maze);
        frame.pack();
        frame.setVisible(true);
        maze.requestFocus();
    }
}
