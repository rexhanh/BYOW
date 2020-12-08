package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Player {
    private int xPos;
    private int yPos;
    private TETile avatar = Tileset.AVATAR;
    private Hallway.Direction direction;

    public Player(int x, int y) {
        this.xPos = x;
        this.yPos = y;
        this.direction = Hallway.Direction.East;
    }

    public void setPos(int x, int y) {
        this.xPos = x;
        this.yPos = y;
    }

    public int getXPos() {
        return this.xPos;
    }

    public int getYPos() {
        return this.yPos;
    }

    public TETile getAvatar() {
        return this.avatar;
    }

    public Hallway.Direction getDirection() {
        return this.direction;
    }

    public void setDirection(char d) {
        switch (d) {
            case 'w':
                this.direction = Hallway.Direction.North;
                break;
            case 'd':
                this.direction = Hallway.Direction.East;
                break;
            case 's':
                this.direction = Hallway.Direction.South;
                break;
            case 'a':
                this.direction = Hallway.Direction.West;
                break;
            default:
                break;
        }
    }
}
