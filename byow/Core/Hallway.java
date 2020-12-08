package byow.Core;

import java.util.Random;

public class Hallway {
    public static final int MIN_LENGTH = 3;
    public static final int MAX_LENGTH = 10;

    public enum Direction {
        North, East, South, West,
    }

    private int hallwayLength;       //Random length of the hallway
    private int startXPos;           //Starting x position for the hallway
    private int startYPos;           //Starting y postion for the hallway
    private Direction direction;     //The direction the hallway is going/heading
    private Random random;
    private Room incidentRoom;
    private Hallway incidentHallway;

    /**
     * Construct a hallway specified by the given room and random.
     * where the hallway starts from a room.
     */
    public Hallway(Room room, Random random) {
        this.random = random;
        this.incidentRoom = room;
        this.incidentHallway = null;
        this.direction = Direction.values()[RandomUtils.uniform(this.random, 0, 4)];
        //generateDirection(room);
        generatePosition(room.getXPos(), room.getYPos(), room.getWidth(), room.getHeight());
    }

    /**
     * Generate the direction of a hallway based on a room.
     */
    private void generateDirection(Room room) {
        int rand = RandomUtils.uniform(this.random, 0, 4);
        if (room.getIncidentHallway() != null) {
            Direction conflict = room.getIncidentHallway().getDirection();
            int a = conflict.ordinal();
            if ((rand + 2) % 4 == a) {
                rand = (rand + 1) % 4;
            }
        }
        this.direction = Direction.values()[rand];
    }

    /**
     * Generate the length, starting xPos, starting yPos of a hallway.
     * based on the given xPos, yPos, width, and height of either a hallway or a room
     */
    private void generatePosition(int xPos, int yPos, int width, int height) {
        int length = RandomUtils.uniform(this.random, Hallway.MIN_LENGTH, Hallway.MAX_LENGTH);

        switch (direction) {
            case North:
                length = Math.min(length, Engine.HEIGHT - yPos - height);
                if (length >= Hallway.MIN_LENGTH) {
                    this.hallwayLength = length;
                    this.startYPos = yPos + height;
                    try {
                        this.startXPos = RandomUtils.uniform(this.random, xPos, xPos + width - 2);
                    } catch (IllegalArgumentException e) {
                        this.clear();
                    }


                } else {
                    this.clear();
                }
                break;
            case East:
                length = Math.min(length, Engine.WIDTH - xPos - width);
                if (length >= Hallway.MIN_LENGTH) {
                    this.hallwayLength = length;
                    this.startXPos = xPos + width;
                    try {
                        this.startYPos = RandomUtils.uniform(this.random, yPos, yPos + height - 2);
                    } catch (IllegalArgumentException e) {
                        this.clear();
                    }

                } else {
                    this.clear();
                }
                break;
            case South:
                length = Math.min(length, yPos);
                if (length >= Hallway.MIN_LENGTH) {
                    this.hallwayLength = length;
                    this.startYPos = yPos - 1;
                    try {
                        this.startXPos = RandomUtils.uniform(this.random, xPos, xPos + width - 2);
                    } catch (IllegalArgumentException e) {
                        this.clear();
                    }

                } else {
                    this.clear();
                }
                break;
            case West:
                length = Math.min(length, xPos);
                if (length >= Hallway.MIN_LENGTH) {
                    this.hallwayLength = length;
                    this.startXPos = xPos - 1;
                    try {
                        this.startYPos = RandomUtils.uniform(this.random, yPos, yPos + height - 2);
                    } catch (IllegalArgumentException e) {
                        this.clear();
                    }

                } else {
                    this.clear();
                }
                break;
            default:
                this.clear();
        }
    }

    private void clear() {
        this.startXPos = 0;
        this.startYPos = 0;
        this.hallwayLength = 0;
    }

    /**
     * Construct a hallway specified by the given hallway and random.
     * where the hallway starts from a hallway.
     */
    public Hallway(Hallway hallway, Random random) {
        this.random = random;
        this.incidentHallway = hallway;
        this.incidentRoom = null;
        generateDirection(hallway);
        int width;
        int height;
        if (hallway.getDirection() == Direction.East || hallway.getDirection() == Direction.West) {
            width = hallway.getLength();
            height = 3;
        } else {
            width = 3;
            height = hallway.getLength();
        }
        if (hallway.getDirection() == Direction.North || hallway.getDirection() == Direction.East) {
            generatePosition(hallway.getStartXPos(), hallway.getStartYPos(), width, height);
        } else if (hallway.getDirection() == Direction.West) {
            generatePosition(hallway.getEndXPos(), hallway.getStartYPos(), width, height);
        } else {
            generatePosition(hallway.getStartXPos(), hallway.getEndYPos(), width, height);
        }
    }

    /**
     * Generate the direction of a hallway based on another hallway.
     */
    private void generateDirection(Hallway hallway) {
        boolean bool = RandomUtils.bernoulli(this.random);
        if (hallway.getDirection() == Direction.East || hallway.getDirection() == Direction.West) {
            if (bool) {
                this.direction = Direction.North;
            } else {
                this.direction = Direction.South;
            }
        } else {
            if (bool) {
                this.direction = Direction.East;
            } else {
                this.direction = Direction.West;
            }
        }
    }

    public int getStartXPos() {
        return this.startXPos;
    }

    public int getStartYPos() {
        return this.startYPos;
    }

    /**
     * Get the of the xPos of the hallway.
     */
    public int getEndXPos() {
        int endX = this.getStartXPos() + 2;
        if (this.direction == Direction.East) {
            endX = this.getStartXPos() + this.getLength() - 1;
        }
        if (this.direction == Direction.West) {
            endX = this.getStartXPos() - this.getLength() + 1;
        }
        return endX;
    }

    public int getEndYPos() {
        int endY = this.getStartYPos() + 2;
        if (this.direction == Direction.North) {
            endY = this.getStartYPos() + this.getLength() - 1;
        }
        if (this.direction == Direction.South) {
            endY = this.getStartYPos() - this.getLength() + 1;
        }
        return endY;
    }

    public int getLength() {
        return this.hallwayLength;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Room getIncidentRoom() {
        return this.incidentRoom;
    }

    public Hallway getIncidentHallway() {
        return this.incidentHallway;
    }
}
