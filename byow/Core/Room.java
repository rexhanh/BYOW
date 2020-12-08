package byow.Core;

import java.util.Random;

public class Room {
    public static final int MIN_SIZE = 5;
    public static final int MAX_SIZE = 8;

    private int xPos;
    private int yPos;
    private int width;
    private int height;
    private Random random;
    private Hallway incidentHallway;

    /**
     * Construct a room just by the given random seed.
     */
    public Room(Random random) {
        this.random = random;
        this.xPos = RandomUtils.uniform(this.random, Engine.WIDTH - Room.MAX_SIZE);
        this.yPos = RandomUtils.uniform(this.random, Engine.HEIGHT - Room.MAX_SIZE);
        this.width = RandomUtils.uniform(this.random, Room.MIN_SIZE, Room.MAX_SIZE);
        this.height = RandomUtils.uniform(this.random, Room.MIN_SIZE, Room.MAX_SIZE);
    }


    /**
     * Construct a room at the end of the hallway.
     * if there are enough space.
     */
    public Room(Hallway hallway, Random random) {
        this.random = random;
        this.incidentHallway = hallway;
        this.generatePosition(hallway);
    }

    /**
     * Generate the position of a room and it's size.
     */
    private void generatePosition(Hallway hallway) {
        int wid = RandomUtils.uniform(this.random, Room.MIN_SIZE, Room.MAX_SIZE);
        int hei = RandomUtils.uniform(this.random, Room.MIN_SIZE, Room.MAX_SIZE);
        switch (hallway.getDirection()) {
            case North:
                hei = Math.min(hei, Engine.HEIGHT - hallway.getEndYPos() - 1);
                if (hei >= Room.MIN_SIZE) {
                    this.setSize(hei, wid);
                    this.yPos = hallway.getEndYPos() + 1;
                    this.setXPos(hallway.getStartXPos(), this.getWidth());
                } else {
                    this.clear();
                }
                break;
            case South:
                hei = Math.min(hei, hallway.getEndYPos());
                if (hei >= Room.MIN_SIZE) {
                    this.setSize(hei, wid);
                    this.yPos = hallway.getEndYPos() - this.getHeight();
                    this.setXPos(hallway.getStartXPos(), this.getWidth());
                } else {
                    this.clear();
                }
                break;
            case East:
                wid = Math.min(wid, Engine.WIDTH - hallway.getEndXPos() - 1);
                if (wid >= Room.MIN_SIZE) {
                    this.setSize(hei, wid);
                    this.xPos = hallway.getEndXPos() + 1;
                    this.setYPos(hallway.getStartYPos(), this.getHeight());
                } else {
                    this.clear();
                }
                break;
            case West:
                wid = Math.min(wid, hallway.getEndXPos());
                if (wid >= Room.MIN_SIZE) {
                    this.setSize(hei, wid);
                    this.xPos = hallway.getEndXPos() - this.getWidth();
                    this.setYPos(hallway.getStartYPos(), this.getHeight());
                } else {
                    this.clear();
                }
                break;
            default:
                this.clear();
        }
    }

    private void setYPos(int x, int y) {
        int alignTo = RandomUtils.uniform(this.random, y - 3);
        int yPosition = x - alignTo;
        if (yPosition >= 0) {
            this.yPos = yPosition;
        } else {
            this.yPos = 0;
        }
    }

    private void setXPos(int x, int y) {
        int alignTo = RandomUtils.uniform(this.random, y - 3);
        int xPosition = x - alignTo;
        if (xPosition >= 0) {
            this.xPos = xPosition;
        } else {
            this.xPos = 0;
        }
    }

    private void setSize(int hei, int wid) {
        this.height = hei;
        this.width = wid;
    }

    private void clear() {
        this.width = 0;
        this.height = 0;
        this.xPos = 0;
        this.yPos = 0;
    }

    public int getXPos() {
        return this.xPos;
    }

    public int getYPos() {
        return this.yPos;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Hallway getIncidentHallway() {
        return this.incidentHallway;
    }
}
