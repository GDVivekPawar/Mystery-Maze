import java.awt.*;
import java.util.List;

public class Player {
    //PlayerData
    Image HeroImg;
    int speed = 5;
    int PosX, PosY;
    int HeroWidth = 32;
    int HeroHeight = 32;

    Player(int boardWidth, int boardHeight){

        PosX = boardWidth/8;
        PosY = boardHeight/2;
    }

    public void moveUp(int x, int y, List<Rectangle> Walls)
    {
        int newY = y + (speed*-1);
        int newX = x;
        Rectangle newBounds = new Rectangle(newX, newY, HeroWidth, HeroHeight);
        boolean collision = false;
        for( Rectangle Wall : Walls)
        {
            if(newBounds.intersects(Wall))
            {
                collision = true;
                break;
            }
        }
        if(!collision)
        {
            PosY = newY;
            PosX = newX;
        }
    }
    public void moveDown(int x, int y, List<Rectangle> Walls)
    {
        int newY = y + (speed*1);
        int newX = x;
        Rectangle newBounds = new Rectangle(newX, newY, HeroWidth, HeroHeight);
        boolean collision = false;
        for( Rectangle Wall : Walls)
        {
            if(newBounds.intersects(Wall))
            {
                collision = true;
                break;
            }
        }
        if(!collision)
        {
            PosY = newY;
            PosX = newX;
        }
    }
    public void moveLeft(int x, int y, List<Rectangle> Walls)
    {
        int newY = y;
        int newX = x + (speed*-1);
        Rectangle newBounds = new Rectangle(newX, newY, HeroWidth, HeroHeight);
        boolean collision = false;
        for( Rectangle Wall : Walls)
        {
            if(newBounds.intersects(Wall))
            {
                collision = true;
                break;
            }
        }
        if(!collision)
        {
            PosY = newY;
            PosX = newX;
        }
    }
    public void moveRight(int x, int y, List<Rectangle> Walls)
    {
        int newY = y;
        int newX = x + (speed*1);
        Rectangle newBounds = new Rectangle(newX, newY, HeroWidth, HeroHeight);
        boolean collision = false;
        for( Rectangle Wall : Walls)
        {
            if(newBounds.intersects(Wall))
            {
                collision = true;
                break;
            }
        }
        if(!collision)
        {
            PosY = newY;
            PosX = newX;
        }
    }
}
