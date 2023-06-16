package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class WorldGenerator {

    private static final int WIDTH = Engine.WIDTH;
    private static final int HEIGHT = Engine.HEIGHT;
    private int rooms;
    private final TETile[][] WORLD;
    private final Random RAND;
    private final int maxSize = 9;
    private final ArrayList<ComparablePoint> roomList;

    public WorldGenerator(long seed, TETile[][] world) {
        this.RAND = new Random(seed);
        this.rooms = (int) RandomUtils.gaussian(RAND, 18, 6) + 3;
        this.WORLD = world;
        this.roomList = new ArrayList<>();
    }

    public TETile[][] generator() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                WORLD[x][y] = Tileset.NOTHING;
            }
        }
        while (rooms > 0) {
            int x = RandomUtils.uniform(RAND, 0, WIDTH - maxSize);
            int y = RandomUtils.uniform(RAND, 0, HEIGHT - maxSize);
            if (WORLD[x][y] == Tileset.NOTHING) {
                if (RAND.nextInt(2) == 0) {
                    roomGenerator(x, y);
                    roomList.add(new ComparablePoint(x, y));
                    rooms--;
                }
            }
        }
        Collections.sort(roomList);
        hallwayGenerator();
        wallGenerator();
        return WORLD;
    }

    static class ComparablePoint extends Point implements Comparable<ComparablePoint>{
        double distanceOrigin = Math.sqrt((this.x * this.x) + (this.y * this.y));

        public ComparablePoint(int x, int y) {
            super(x, y);
        }

        public int compareTo(ComparablePoint other){
            if (distanceOrigin < other.distanceOrigin) {
                return -1;
            } else if (distanceOrigin == other.distanceOrigin) {
                return 0;
            }
            return 1;
        }
    }

    private void roomGenerator(int x, int y) {
        int sizeX = RandomUtils.uniform(RAND, 3, maxSize);
        int sizeY = RandomUtils.uniform(RAND, 3, maxSize);
        for (int i = x; i < x + sizeX; i++) {
            for (int j = y; j < y + sizeY; j++) {
                WORLD[i][j] = Tileset.FLOOR;
            }
        }
    }

    private void hallwayGenerator() {
        for (int i = 0; i < roomList.size() - 1; i++) {
            Point p1 = roomList.get(i);
            Point p2 = roomList.get(i + 1);
            int x1 = (int) p1.getX();
            int x2 = (int) p2.getX();
            int y1 = (int) p1.getY();
            int y2 = (int) p2.getY();
            if (x1 <= x2 && y1 <= y2) {
                hallway1(x1, y1, x2, y2);
            } else if (x1 > x2 && y1 < y2) {
                hallway2(x2, y2, x1, y1);
            } else if (x1 <= x2) {
                hallway2(x1, y1, x2, y2);
            } else if (y1 > y2) {
                hallway1(x2, y2, x1, y1);
            }
        }
    }

    private void hallway1(int x1, int y1, int x2, int y2) {
        while (x1 <= x2) {
            WORLD[x1][y1] = Tileset.FLOOR;
            x1++;
        }
        while (y1 <= y2) {
            WORLD[x1][y1] = Tileset.FLOOR;
            y1++;
        }
    }

    private void hallway2(int x1, int y1, int x2, int y2) {
        while (x1 <= x2) {
            WORLD[x1][y1] = Tileset.FLOOR;
            x1++;
        }
        while (y1 >= y2) {
            WORLD[x1][y1] = Tileset.FLOOR;
            y1--;
        }
    }

    private void wallGenerator() {
        TETile colored = TETile.colorVariant(Tileset.WALL, 255, 255, 255, RAND);
        wallInner(colored);
        wallOuter(colored);
    }

    private void wallInner(TETile wall) {
        for (int x = 1; x < WIDTH - 1; x++) {
            for (int y = 1; y < HEIGHT - 1; y++) {
                if (WORLD[x][y] == Tileset.NOTHING && (WORLD[x + 1][y - 1] == Tileset.FLOOR ||
                        WORLD[x + 1][y] == Tileset.FLOOR || WORLD[x + 1][y + 1] == Tileset.FLOOR ||
                        WORLD[x][y - 1] == Tileset.FLOOR || WORLD[x][y + 1] == Tileset.FLOOR ||
                        WORLD[x - 1][y - 1] == Tileset.FLOOR || WORLD[x - 1][y] == Tileset.FLOOR ||
                        WORLD[x - 1][y + 1] == Tileset.FLOOR)) {
                    WORLD[x][y] = TETile.colorVariant(wall, 64, 64, 64, RAND);
                }
            }
        }
    }

    private void wallOuter(TETile wall) {
        // bottom most
        for (int x = 1; x < WIDTH - 1; x++) {
            if (WORLD[x + 1][1] == Tileset.FLOOR || WORLD[x][1] == Tileset.FLOOR || WORLD[x - 1][1] == Tileset.FLOOR) {
                WORLD[x][0] = TETile.colorVariant(wall, 64, 64, 64, RAND);
            }
        }
        // top most
        for (int x = 1; x < WIDTH - 1; x++) {
            if (WORLD[x + 1][HEIGHT - 2] == Tileset.FLOOR || WORLD[x][HEIGHT - 2] == Tileset.FLOOR ||
                    WORLD[x - 1][HEIGHT - 2] == Tileset.FLOOR) {
                WORLD[x][HEIGHT - 1] = TETile.colorVariant(wall, 64, 64, 64, RAND);
            }
        }
        // left most
        for (int y = 1; y < HEIGHT - 1; y++) {
            if (WORLD[1][y + 1] == Tileset.FLOOR || WORLD[1][y] == Tileset.FLOOR || WORLD[1][y - 1] == Tileset.FLOOR) {
                WORLD[0][y] = TETile.colorVariant(wall, 64, 64, 64, RAND);
            }
        }
        // right most
        for (int y = 1; y < HEIGHT - 1; y++) {
            if (WORLD[WIDTH - 2][y + 1] == Tileset.FLOOR || WORLD[WIDTH - 2][y] == Tileset.FLOOR ||
                    WORLD[WIDTH - 2][y - 1] == Tileset.FLOOR) {
                WORLD[WIDTH - 1][y] = TETile.colorVariant(wall, 64, 64, 64, RAND);
            }
        }
        // corners
        if (WORLD[1][1] == Tileset.FLOOR) WORLD[0][0] = TETile.colorVariant(wall, 64, 64, 64, RAND);
        if (WORLD[1][HEIGHT - 2] == Tileset.FLOOR) WORLD[0][HEIGHT - 1] = TETile.colorVariant(wall, 64, 64, 64, RAND);
        if (WORLD[WIDTH - 2][1] == Tileset.FLOOR) WORLD[WIDTH - 1][0] = TETile.colorVariant(wall, 64, 64, 64, RAND);
        if (WORLD[WIDTH - 2][HEIGHT - 2] == Tileset.FLOOR) WORLD[WIDTH - 1][HEIGHT - 1] = TETile.colorVariant(wall, 64, 64, 64, RAND);
    }
}
