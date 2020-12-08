package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    private TETile[][] world;
    private List<Room> rooms;
    private List<Hallway> hallways;
    private Random random;
    private Player player;
    private boolean switchOn;
    private boolean doorOpen;

    private String fileName = "saved.txt";

    /**
     * Setting up the empty Tile 2D grid.
     */
    private void setupTileGrid() {
        this.world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.GRASS;
            }
        }
    }

    /**
     * Randomly generate an array of rooms and hallways.
     */
    private void generateRoomAndHallway() {
        this.rooms = new ArrayList<>();
        this.hallways = new ArrayList<>();

        this.rooms.add(new Room(this.random));
        int min = Engine.WIDTH / Room.MAX_SIZE;
        int max = 2 * Engine.WIDTH / Room.MAX_SIZE;
        int roomNum = RandomUtils.uniform(this.random, min, max);
        int maxNumOfIteration = 15000;
        while (this.rooms.size() < roomNum && maxNumOfIteration > 0) {
            boolean bool = RandomUtils.bernoulli(this.random);
            Hallway newhallway = new Hallway(this.rooms.get(this.rooms.size() - 1), this.random);
            if (checkHallway(newhallway)) {
                this.hallways.add(newhallway);
            }
            if (bool && this.hallways.size() > 0
                    && this.hallways.get(this.hallways.size() - 1) != null) {
                Hallway newhallway1 = new Hallway(hallways.get(hallways.size() - 1), this.random);
                if (checkHallway(newhallway1)) {
                    this.hallways.add(newhallway1);
                }
            }
            if (this.hallways.size() > 0) {
                Room newroom = new Room(this.hallways.get(this.hallways.size() - 1), this.random);
                if (checkRoom(newroom)) {
                    this.rooms.add(newroom);
                }
            }
            maxNumOfIteration -= 1;
        }
    }


    /**
     * Check if a room is valid specified by the following properties:
     * the height and the width of the room can't be 0
     * the area covered by the room can not exceed the grid
     * the room does not overlap with any room in the array rooms.
     */
    private boolean checkRoom(Room room) {
        if (room.getWidth() == 0 || room.getXPos() + room.getWidth() > Engine.WIDTH
                || room.getYPos() + room.getHeight() > Engine.HEIGHT) {
            return false;
        }
        for (Room r : this.rooms) {
            if (checkRoomConflicts(r, room) || checkRoomConflicts(room, r)) {
                return false;
            }
        }
        for (Hallway h : this.hallways) {
            if (checkRoomHallwayConflicts(room, h) || checkRoomHallwayConflicts(h, room)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the second room overlaps with the first room.
     * return true if there is no overlap
     */
    private boolean checkRoomConflicts(Room room1, Room room2) {
        int startX2 = room2.getXPos();
        int startY2 = room2.getYPos();
        int endX2 = room2.getXPos() + room2.getWidth() - 1;
        int endY2 = room2.getYPos() + room2.getHeight() - 1;

        boolean bool1 = inRoom(room1, startX2, startY2);
        boolean bool2 = inRoom(room1, startX2, endY2);
        boolean bool3 = inRoom(room1, endX2, startY2);
        boolean bool4 = inRoom(room1, endX2, endY2);

        return bool1 || bool2 || bool3 || bool4;
    }

    /**
     * Check if the given point (x, y) is inside room
     */
    private boolean inRoom(Room room, int x, int y) {
        return x >= room.getXPos() && x < room.getXPos() + room.getWidth()
                && y >= room.getYPos() && y < room.getYPos() + room.getHeight();
    }


    /**
     * Check if the hallway is valid specified by the following properties:
     * the length of the hallway can't be 0
     * the hallway can't exceed the grid
     * the hallway does not overlap any hallway in the array of hallways
     */
    private boolean checkHallway(Hallway hallway) {
        if (hallway.getLength() == 0 || hallway.getEndXPos() > Engine.WIDTH
                || hallway.getEndXPos() < 0 || hallway.getEndYPos() > Engine.HEIGHT
                || hallway.getEndYPos() < 0) {
            return false;
        }
        for (Hallway h : this.hallways) {
            if (checkHallwayConflicts(h, hallway) || checkHallwayConflicts(hallway, h)) {
                return false;
            }
        }
        for (Room r : this.rooms) {
            if (checkRoomHallwayConflicts(r, hallway) || checkRoomHallwayConflicts(hallway, r)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the hallway overlaps the room.
     */
    private boolean checkRoomHallwayConflicts(Room room, Hallway hallway) {
        int startX2 = hallway.getStartXPos();
        int startY2 = hallway.getStartYPos();
        int endX2 = hallway.getEndXPos();
        int endY2 = hallway.getEndYPos();

        boolean bool1 = inRoom(room, startX2, startY2);
        boolean bool2 = inRoom(room, startX2, endY2);
        boolean bool3 = inRoom(room, endX2, startY2);
        boolean bool4 = inRoom(room, endX2, endY2);

        return bool1 || bool2 || bool3 || bool4;
    }

    private boolean checkRoomHallwayConflicts(Hallway hallway, Room room) {
        int startX2 = room.getXPos();
        int startY2 = room.getYPos();
        int endX2 = room.getXPos() + room.getWidth() - 1;
        int endY2 = room.getYPos() + room.getHeight() - 1;

        boolean bool1 = inHallway(hallway, startX2, startY2);
        boolean bool2 = inHallway(hallway, startX2, endY2);
        boolean bool3 = inHallway(hallway, endX2, startY2);
        boolean bool4 = inHallway(hallway, endX2, endY2);

        return bool1 || bool2 || bool3 || bool4;
    }


    /**
     * Check if the second hallway overlaps the first hallway.
     */
    private boolean checkHallwayConflicts(Hallway hallway1, Hallway hallway2) {
        int startX2 = hallway2.getStartXPos();
        int startY2 = hallway2.getStartYPos();
        int endX2 = hallway2.getEndXPos();
        int endY2 = hallway2.getEndYPos();

        boolean bool1 = inHallway(hallway1, startX2, startY2);
        boolean bool2 = inHallway(hallway1, startX2, endY2);
        boolean bool3 = inHallway(hallway1, endX2, startY2);
        boolean bool4 = inHallway(hallway1, endX2, endY2);

        return bool1 || bool2 || bool3 || bool4;
    }

    /**
     * Check if the given point(x, y) is in the hallway.
     */
    private boolean inHallway(Hallway hallway, int x, int y) {
        int maxX = Math.max(hallway.getStartXPos(), hallway.getEndXPos());
        int minX = Math.min(hallway.getStartXPos(), hallway.getEndXPos());
        int maxY = Math.max(hallway.getStartYPos(), hallway.getEndYPos());
        int minY = Math.min(hallway.getStartYPos(), hallway.getEndYPos());

        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }


    private void setFloors() {
        addRoomFloor();
        addHallwayFloor();
    }

    /**
     * Add floors to all the rooms in the list.
     */
    private void addRoomFloor() {
        for (Room room : this.rooms) {
            for (int i = room.getXPos() + 1; i < room.getXPos() + room.getWidth() - 1; i++) {
                for (int j = room.getYPos() + 1; j < room.getYPos() + room.getHeight() - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
            if (room.getIncidentHallway() != null) {
                Hallway incident = room.getIncidentHallway();
                switch (incident.getDirection()) {
                    case North:
                        world[incident.getEndXPos() - 1][incident.getEndYPos() + 1] = Tileset.FLOOR;
                        world[incident.getEndXPos() - 1][incident.getEndYPos()] = Tileset.FLOOR;

                        break;
                    case East:
                        world[incident.getEndXPos() + 1][incident.getEndYPos() - 1] = Tileset.FLOOR;
                        world[incident.getEndXPos()][incident.getEndYPos() - 1] = Tileset.FLOOR;
                        break;
                    case South:
                        world[incident.getEndXPos() - 1][incident.getEndYPos() - 1] = Tileset.FLOOR;
                        world[incident.getEndXPos() - 1][incident.getEndYPos()] = Tileset.FLOOR;

                        break;
                    case West:
                        world[incident.getEndXPos() - 1][incident.getEndYPos() - 1] = Tileset.FLOOR;
                        world[incident.getEndXPos()][incident.getEndYPos() - 1] = Tileset.FLOOR;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Add floors to all the hallways in the list.
     */
    private void addHallwayFloor() {
        for (Hallway hallway : this.hallways) {
            switch (hallway.getDirection()) {
                case North:
                    for (int i = hallway.getStartYPos(); i < hallway.getEndYPos(); i++) {
                        world[hallway.getStartXPos() + 1][i] = Tileset.FLOOR;
                    }
                    world[hallway.getStartXPos() + 1][hallway.getStartYPos() - 1] = Tileset.FLOOR;
                    break;
                case East:
                    for (int i = hallway.getStartXPos(); i < hallway.getEndXPos(); i++) {
                        world[i][hallway.getStartYPos() + 1] = Tileset.FLOOR;
                    }
                    world[hallway.getStartXPos() - 1][hallway.getStartYPos() + 1] = Tileset.FLOOR;
                    break;
                case South:
                    for (int i = hallway.getEndYPos() + 1; i <= hallway.getStartYPos(); i++) {
                        world[hallway.getStartXPos() + 1][i] = Tileset.FLOOR;
                    }
                    world[hallway.getStartXPos() + 1][hallway.getStartYPos() + 1] = Tileset.FLOOR;
                    break;
                case West:
                    for (int i = hallway.getEndXPos() + 1; i <= hallway.getStartXPos(); i++) {
                        world[i][hallway.getStartYPos() + 1] = Tileset.FLOOR;
                    }
                    world[hallway.getStartXPos() + 1][hallway.getStartYPos() + 1] = Tileset.FLOOR;
                    break;
                default:
                    break;
            }

        }
    }


    private void setWalls() {
        addRoomWalls();
        addHallwayWalls();
    }

    /**
     * Add walls to all rooms in the list.
     */
    private void addRoomWalls() {
        for (Room room : this.rooms) {
            addWalls(room.getXPos(), room.getXPos() + room.getWidth() - 1,
                    room.getYPos(), room.getYPos() + room.getHeight() - 1);
        }
    }

    /**
     * Add walls to the given grid specified by the two positions.
     * with both starting and ending position included.
     */
    private void addWalls(int startX, int endX, int startY, int endY) {
        for (int i = startX; i <= endX; i++) {
            if (world[i][startY] != Tileset.FLOOR) {
                world[i][startY] = Tileset.WALL;
            }
            if (world[i][endY] != Tileset.FLOOR) {
                world[i][endY] = Tileset.WALL;
            }
        }
        for (int i = startY + 1; i <= endY - 1; i++) {
            if (world[startX][i] != Tileset.FLOOR) {
                world[startX][i] = Tileset.WALL;
            }
            if (world[endX][i] != Tileset.FLOOR) {
                world[endX][i] = Tileset.WALL;
            }
        }
    }

    /**
     * Add walls to all hallways in the list.
     */
    private void addHallwayWalls() {
        for (Hallway hallway : this.hallways) {
            int startX = Math.min(hallway.getStartXPos(), hallway.getEndXPos());
            int endX = Math.max(hallway.getStartXPos(), hallway.getEndXPos());
            int startY = Math.min(hallway.getStartYPos(), hallway.getEndYPos());
            int endY = Math.max(hallway.getStartYPos(), hallway.getEndYPos());
            addWalls(startX, endX, startY, endY);
        }

    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     *
     * @Surce win.png is from google image.
     */
    public void interactWithKeyboard() {
        this.ter.initialize(Engine.WIDTH, Engine.HEIGHT + 2, 0, 0);
        menu();
        boolean bool = true;
        String seed = null;
        while (bool) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (c == 'l') {
                    seed = this.load();
                }
                this.nql(c);
                bool = false;
                if (c == 'n') {
                    seed = this.seed();
                    StdDraw.clear(Color.BLACK);
                    this.interactWithInputString(seed);
                    this.show();
                }
            }
        }
        boolean bool1 = true;
        while (bool1) {
            this.hud();
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (c == 'q' && seed.charAt(seed.length() - 1) == ':') {
                    bool1 = false;
                    String s = seed.substring(0, seed.length() - 1);
                    if (s != null) {
                        this.saveAndQuit(s);
                    }
                    System.exit(0);

                } else if (c != ':') {
                    this.move(c);
                    this.show();

                }
                seed += c;
            }
            if (switchOn) {
                if (doorOpen) {
                    StdDraw.picture(WIDTH / 2, HEIGHT / 2, "assets/win.png");
                    StdDraw.show();
                    break;
                }
            }

        }
    }

    /**
     * Only show what are accessible to the avatar.
     * if the switch is on, render the whole frame
     * otherwise only render tiles around the avatar.
     */
    private void show() {
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);
        if (this.switchOn) {
            this.ter.renderFrame(this.world);
        } else {
            int xPos = this.player.getXPos();
            int yPos = this.player.getYPos();
            Hallway.Direction direction = this.player.getDirection();

            switch (direction) {
                case East:
                    for (int i = xPos - 1; i < xPos + 8; i++) {
                        if (i < Engine.WIDTH) {
                            this.world[i][yPos].draw(i, yPos);
                            this.world[i][yPos + 1].draw(i, yPos + 1);
                            this.world[i][yPos - 1].draw(i, yPos - 1);
                        } else {
                            break;
                        }
                    }

                    break;
                case South:
                    for (int i = yPos + 1; i > yPos - 8; i--) {
                        if (i >= 0) {
                            this.world[xPos][i].draw(xPos, i);
                            this.world[xPos - 1][i].draw(xPos - 1, i);
                            this.world[xPos + 1][i].draw(xPos + 1, i);
                        } else {
                            break;
                        }
                    }

                    break;
                case West:
                    for (int i = xPos + 1; i > xPos - 8; i--) {
                        if (i >= 0) {
                            this.world[i][yPos].draw(i, yPos);
                            this.world[i][yPos + 1].draw(i, yPos + 1);
                            this.world[i][yPos - 1].draw(i, yPos - 1);
                        } else {
                            break;
                        }
                    }

                    break;
                case North:
                    for (int i = yPos - 1; i < yPos + 8; i++) {
                        if (i < Engine.HEIGHT) {
                            this.world[xPos][i].draw(xPos, i);
                            this.world[xPos - 1][i].draw(xPos - 1, i);
                            this.world[xPos + 1][i].draw(xPos + 1, i);
                        } else {
                            break;
                        }
                    }

                    break;
                default:
                    break;
            }
            StdDraw.show();
        }

    }


    /**
     * Take in user input of numbers as a random seed.
     */
    private String seed() {
        boolean bool = true;
        StringBuffer sbf = new StringBuffer();
        while (bool) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (c == 's') {
                    bool = false;
                } else {
                    sbf.append(c);
                }

                StdDraw.enableDoubleBuffering();
                StdDraw.clear(Color.BLACK);
                StdDraw.text(Engine.WIDTH * 0.5, Engine.HEIGHT * 0.75, "Enter a random number");
                StdDraw.text(Engine.WIDTH * 0.5, Engine.HEIGHT * 0.5, sbf.toString());
                StdDraw.show();
            }
        }
        return "n" + sbf.toString() + "s";
    }

    /**
     * Display different screens based on user input.
     */
    private void nql(char c) {
        StdDraw.clear(Color.BLACK);
        switch (c) {
            case 'q':
                System.exit(0);
                break;
            case 'l':
                this.ter.renderFrame(this.interactWithInputString("l"));
                break;
            case 'n':
                StdDraw.text(Engine.WIDTH * 0.5, Engine.HEIGHT * 0.75, "Enter a random number");
                StdDraw.show();
                break;
            default:
                break;
        }
    }

    /**
     * Head up display.
     */
    private void hud() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();

        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);
        this.show();

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textRight(Engine.WIDTH - 3, Engine.HEIGHT + 1, "Quit :q");


        if (x < Engine.WIDTH && y < Engine.HEIGHT && switchOn) {
            StdDraw.textLeft(0, Engine.HEIGHT + 1, this.world[x][y].description());
        } else if (!switchOn) {
            Hallway.Direction d = this.player.getDirection();
            int xPos = this.player.getXPos();
            int yPos = this.player.getYPos();
            switch (d) {
                case North:
                    if (x >= xPos - 1 && x <= xPos + 1 && y >= yPos - 1 && y < yPos + 8) {
                        StdDraw.textLeft(0, Engine.HEIGHT + 1, this.world[x][y].description());
                    }
                    break;
                case East:
                    if (x >= xPos - 1 && x < xPos + 8 && y >= yPos - 1 && y <= yPos + 1) {
                        StdDraw.textLeft(0, Engine.HEIGHT + 1, this.world[x][y].description());
                    }
                    break;
                case South:
                    if (x >= xPos - 1 && x <= xPos + 1 && y > yPos - 8 && y <= yPos + 1) {
                        StdDraw.textLeft(0, Engine.HEIGHT + 1, this.world[x][y].description());
                    }
                    break;
                case West:
                    if (x > xPos - 8 && x <= xPos + 1 && y >= yPos - 1 && y <= yPos + 1) {
                        StdDraw.textLeft(0, Engine.HEIGHT + 1, this.world[x][y].description());
                    }
                    break;
                default:
                    break;
            }

        }
        StdDraw.show();
        StdDraw.pause(10);


    }

    /**
     * Set up Menu.
     */
    private void menu() {
        StdDraw.setPenColor(Color.WHITE);
        Font title = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(title);
        StdDraw.text(Engine.WIDTH / 2.0, Engine.HEIGHT * 0.75, "The Weird World");

        Font heading = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(heading);
        StdDraw.text(Engine.WIDTH * 0.5, Engine.HEIGHT * 0.5, "Load Game (L)");
        StdDraw.text(Engine.WIDTH * 0.5, Engine.HEIGHT * 0.5 + 2, "New Game (N)");
        StdDraw.text(Engine.WIDTH * 0.5, Engine.HEIGHT * 0.5 - 2, "Quit (Q)");

        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    /**
     * Swap the position of the player with the given point (x, y).
     */
    private void swap(Player p, int x, int y) {
        TETile tile = this.world[x][y];

        if (tile == Tileset.FLOOR) {
            this.world[x][y] = p.getAvatar();
            this.world[p.getXPos()][p.getYPos()] = Tileset.FLOOR;
            p.setPos(x, y);
        } else if (tile == Tileset.LOCKED_DOOR) {
            this.world[x][y] = Tileset.UNLOCKED_DOOR;
            doorOpen = true;
        } else if (tile == Tileset.UNLOCKED_DOOR) {
            this.world[x][y] = Tileset.LOCKED_DOOR;
            doorOpen = false;
        } else if (tile == Tileset.SWITCH) {
            this.switchOn = !this.switchOn;
        }
    }

    /**
     * Add a player to the world.
     */
    private void addPlayer() {
        int i = RandomUtils.uniform(random, this.rooms.size());
        Room r = this.rooms.get(i);

        int x = RandomUtils.uniform(random, r.getXPos() + 1, r.getXPos() + r.getWidth() - 2);
        int y = RandomUtils.uniform(random, r.getYPos() + 1, r.getYPos() + r.getHeight() - 2);

        this.player = new Player(x, y);
        this.world[x][y] = this.player.getAvatar();
    }

    /**
     * Move the player at the given direction, if the target position is not a wall.
     */
    private void move(char c) {
        this.player.setDirection(c);
        switch (c) {
            case 'w':
                swap(this.player, this.player.getXPos(), this.player.getYPos() + 1);
                break;
            case 'd':
                swap(this.player, this.player.getXPos() + 1, this.player.getYPos());
                break;
            case 's':
                swap(this.player, this.player.getXPos(), this.player.getYPos() - 1);
                break;
            case 'a':
                swap(this.player, this.player.getXPos() - 1, this.player.getYPos());
                break;
            default:
                break;
        }
    }


    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        input = input.toLowerCase();
        char first = input.charAt(0);
        if (first == 'l') {
            String sbf = this.load();
            input = sbf + input.substring(1);
        }

        String s = input.substring(input.indexOf('n') + 1, input.indexOf('s'));
        long seed = Long.parseLong(s);
        this.random = new Random(seed);

        String afterSeed = input.substring(s.length() + 1);
        setupTileGrid();
        generateRoomAndHallway();
        setFloors();
        setWalls();
        addPlayer();
        addTrees();
        addDoorAndSwitch();


        for (int i = 0; i < afterSeed.length(); i++) {
            char c = afterSeed.charAt(i);
            if (c == ':') {
                if (afterSeed.charAt(i + 1) == 'q') {
                    this.saveAndQuit(input.substring(0, input.indexOf(':')));
                }
            } else {
                this.move(c);
            }

        }

        TETile[][] finalWold = this.world;
        return finalWold;
    }

    /**
     * Add tree randomly to our grid.
     * because tree is larger than a tile, so simply cover any grass.
     */
    private void addTrees() {
        int numOfTrees = RandomUtils.uniform(this.random, 30, 80);
        int numOfIterations = 500;
        int trees = 0;
        while (trees < numOfTrees && numOfIterations > 0) {
            int x = RandomUtils.uniform(this.random, Engine.WIDTH);
            int y = RandomUtils.uniform(this.random, Engine.HEIGHT);
            if (this.world[x][y] == Tileset.GRASS) {
                this.world[x][y] = Tileset.TREE;
                trees += 1;
            }
            numOfIterations -= 1;
        }

    }

    /**
     * Add a locked door to our grid.
     */
    private void addDoorAndSwitch() {
        boolean bool = true;
        while (bool) {
            int r = RandomUtils.uniform(this.random, this.rooms.size());
            Room room = this.rooms.get(r);
            int dx = RandomUtils.uniform(this.random, room.getXPos() + 1,
                    room.getXPos() + room.getWidth() - 1);
            int dy = room.getYPos();
            if (this.world[dx][dy] != Tileset.FLOOR) {
                this.world[dx][dy] = Tileset.LOCKED_DOOR;
                bool = false;
            }

        }
        this.doorOpen = false;

        int h;
        Hallway hallway = null;
        boolean bool2 = true;
        while (bool2) {
            h = RandomUtils.uniform(this.random, this.hallways.size());
            if (this.hallways.get(h).getLength() > 3) {
                hallway = this.hallways.get(h);
                bool2 = false;
            }
        }

        Hallway.Direction d = hallway.getDirection();

        boolean bool1 = true;
        while (bool1) {
            switch (d) {
                case North:
                    int sy = RandomUtils.uniform(this.random, hallway.getStartYPos() + 1,
                            hallway.getEndYPos());
                    if (world[hallway.getStartXPos()][sy] != Tileset.FLOOR) {
                        world[hallway.getStartXPos()][sy] = Tileset.SWITCH;
                        bool1 = false;
                    }
                    break;
                case East:
                    int sx = RandomUtils.uniform(this.random, hallway.getStartXPos() + 1,
                            hallway.getEndXPos());
                    if (world[sx][hallway.getEndYPos()] != Tileset.FLOOR) {
                        world[sx][hallway.getEndYPos()] = Tileset.SWITCH;
                        bool1 = false;
                    }
                    break;
                case South:
                    int sy1 = RandomUtils.uniform(this.random, hallway.getEndYPos() + 1,
                            hallway.getStartYPos());
                    if (world[hallway.getEndXPos()][sy1] != Tileset.FLOOR) {
                        world[hallway.getEndXPos()][sy1] = Tileset.SWITCH;
                        bool1 = false;
                    }
                    break;
                case West:
                    int sx1 = RandomUtils.uniform(this.random, hallway.getEndXPos() + 1,
                            hallway.getStartXPos());
                    if (world[sx1][hallway.getStartYPos()] != Tileset.FLOOR) {
                        world[sx1][hallway.getStartYPos()] = Tileset.SWITCH;
                        bool1 = false;
                    }
                    break;
                default:
                    break;

            }
        }
    }

    /**
     * Save the given string in a file.
     *
     * @Source https://caveofprogramming.com/java/java-file-reading-and-writing-files-in-java.html
     */
    private void saveAndQuit(String s) {
        try {
            FileWriter fileWriter = new FileWriter(this.fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(s);
            bufferedWriter.close();
        } catch (IOException ex) {
            System.out.println("Error writing file '" + fileName + "'");
        }
    }

    /**
     * Read the content from a file.
     *
     * @Source https://caveofprogramming.com/java/java-file-reading-and-writing-files-in-java.html
     */
    private String load() {
        String line;
        try {
            FileReader fileReader = new FileReader(this.fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer s = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                s.append(line);
            }
            bufferedReader.close();
            return s.toString();
        } catch (IOException e) {
            return null;
        }

    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT + 2, 0, 0);
        Engine eng = new Engine();

        eng.interactWithKeyboard();
    }

}
