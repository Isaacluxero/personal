package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class Engine {
    TERenderer ter = new TERenderer();
    Random RAND;
    /* Store the questions and answers for encounters. */
    List<String> sortTypes;
    List<String> conditionTypes;
    Map<String, Integer> answer;
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    /* Records the current STATUS: < 0 - haven't started, 0 - game over, > 0 - in progress. */
    public int STATUS = -1;
    public String inputMethod;
    public String input = "";
    public TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
    public TETile[][] encounterPage = new TETile[WIDTH][HEIGHT];
    public static final Font fontBig = new Font("Sans Serif", Font.PLAIN, 30);
    public static final Font fontSmall = new Font("Sans Serif", Font.PLAIN, 20);
    public String SEED = "";
    public int avatarX;
    public int avatarY;
    public int avatarEX;
    public int avatarEY;
    public int encounterX;
    public int encounterY;
    public int encounters;
    public int lives = 5;
    File inputFile = new File("./input.txt");

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        inputMethod = "keyboard";
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(fontBig);
        StdDraw.text(0.5, 0.7, "CS61BL: THE GAME");
        StdDraw.setFont(fontSmall);
        StdDraw.text(0.5, 0.55, "New Game (N)");
        StdDraw.text(0.5, 0.5, "Load Game (L)");
        StdDraw.text(0.5, 0.45, "Replay Save (R)");
        StdDraw.text(0.5, 0.40, "Quit (Q)");

        while (STATUS != 0) {
            if (StdDraw.hasNextKeyTyped()) keyPress(String.valueOf(StdDraw.nextKeyTyped()));
            if (STATUS == 1) {
                if (lives <= 0) {
                    StdDraw.clear(Color.black);
                    StdDraw.setPenColor(Color.red);
                    StdDraw.setFont(fontBig);
                    StdDraw.text(WIDTH / 2, HEIGHT / 2, "YOU DIED");
                    StdDraw.show();
                    STATUS = 0;
                    while (true) {
                        double x = RandomUtils.gaussian(new Random(),WIDTH / 2, WIDTH / 5);
                        double y = RandomUtils.gaussian(new Random(),HEIGHT / 2, HEIGHT / 5);
                        StdDraw.text(x, y, "＜○＞");
                        StdDraw.show();
                        StdDraw.pause((int) Math.abs(RandomUtils.gaussian(new Random(),5, 1)));
                    }
                } else if (encounters == 0) {
                    StdDraw.clear(Color.black);
                    StdDraw.setPenColor(Color.yellow);
                    StdDraw.setFont(fontBig);
                    StdDraw.text(WIDTH / 2, HEIGHT / 2, "VICTORY ACHIEVED");
                    StdDraw.show();
                    STATUS = 0;
                } else {
                    ter.renderFrame(finalWorldFrame, lives);
                }
            }
        }
    }

    public void keyPress(String key) {
        input += key.toLowerCase(Locale.ROOT);
        if (STATUS == 1) {
            if (key.charAt(0) == 'w') {
                moveHelper(0, 1);
            } else if (key.charAt(0) == 's') {
                moveHelper(0, -1);
            } else if (key.charAt(0) == 'a') {
                moveHelper(-1, 0);
            } else if (key.charAt(0) == 'd') {
                moveHelper(1, 0);
            } else if ((input.length() >= 2 && key.charAt(0) == 'q') &&
                    input.charAt(input.length() - 2) == ':') {
                Utils.writeContents(inputFile, input.substring(0, input.length() - 2));
                STATUS = 0;
                if (inputMethod.equals("keyboard")) {
                    System.exit(0);
                }
            }
        } else if (STATUS == 2) {
            if (key.charAt(0) == 'w') {
                eMoveHelper(0, 1);
            } else if (key.charAt(0) == 's') {
                eMoveHelper(0, -1);
            } else if (key.charAt(0) == 'a') {
                eMoveHelper(-1, 0);
            } else if (key.charAt(0) == 'd') {
                eMoveHelper(1, 0);
            } else if ((input.length() >= 2 && key.charAt(0) == 'q') &&
                    input.charAt(input.length() - 2) == ':') {
                Utils.writeContents(inputFile, input.substring(0, input.length() - 2));
                STATUS = 0;
                if (inputMethod.equals("keyboard")) {
                    System.exit(0);
                }
            }
        } else if (STATUS == -2) {
            if ((key.charAt(0) =='s')) {
                STATUS = 1;
                if (SEED.isBlank()) {
                    SEED = "0";
                }
                worldGenerator(SEED);
                if (inputMethod.equals("keyboard")) {
                    StdDraw.clear(Color.BLACK);
                    StdDraw.setPenColor(Color.red);
                    StdDraw.setFont(fontBig);
                    StdDraw.text(0.5, 0.6, "THE WALLS ARE WATCHING,");
                    StdDraw.pause(1000);
                    StdDraw.text(0.5, 0.5, "DON'T TOUCH THEM");
                    StdDraw.pause(1000);
                    StdDraw.text(0.5, 0.4, "その目だれの目?");
                    int eye = 0;
                    while (eye < 300) {
                        double x = RandomUtils.gaussian(new Random(),0.5, 0.3);
                        double y = RandomUtils.gaussian(new Random(),0.5, 0.3);
                        StdDraw.text(x, y, "＜○＞");
                        StdDraw.pause((int) Math.abs(RandomUtils.gaussian(new Random(),10, 5)));
                        eye++;
                    }
                    ter.initialize(WIDTH, HEIGHT + 1, 0, 1);
                }
            } else if (Character.isDigit(key.charAt(0))) {
                SEED += key.charAt(0);
                if (inputMethod.equals("keyboard")) drawSeedInputPage(SEED);
            }
        } else if (STATUS == -1) {
            if (key.charAt(0) == 'n') {
                STATUS = -2;
                if (inputMethod.equals("keyboard")) drawSeedInputPage("");
            } else if (key.charAt(0) == 'l') {
                input = "";
                if (loadHelper()) {
                    String input = Utils.readContentsAsString(inputFile);
                    if (inputMethod.equals("keyboard")) {
                        interactWithInputString(input);
                        inputMethod = "keyboard";
                        ter.initialize(WIDTH, HEIGHT + 1, 0, 1);
                    } else {
                        interactWithInputString(input);
                    }
                    STATUS = 1;
                }
            } else if (key.charAt(0) == 'r') {
                input = "";
                replay();
                inputMethod = "keyboard";
            } else if (key.charAt(0) == 'q') {
                STATUS = 0;
                if (inputMethod.equals("keyboard")) {
                    System.exit(0);
                }
            }
        }
    }

    private void worldGenerator(String SEED) {
        RAND = new Random(Long.parseLong(SEED));
        WorldGenerator gen = new WorldGenerator(Long.parseLong(SEED), finalWorldFrame);
        finalWorldFrame = gen.generator();
        /* Generate random position for avatar. */
        while (finalWorldFrame[avatarX][avatarY] != Tileset.FLOOR) {
            avatarX = RandomUtils.uniform(RAND, 0, WIDTH);
            avatarY = RandomUtils.uniform(RAND, 0, HEIGHT);
        }
        finalWorldFrame[avatarX][avatarY] = Tileset.AVATAR;
        /* Generate random position for encounters. */
        encounters = (int) RandomUtils.gaussian(RAND, 5, 2) + 3;
        int eyeballs = 0;
        while (eyeballs < encounters) {
            encounterGenerator();
            eyeballs++;
        }
    }

    private void encounterGenerator() {
        /* Set up the questions for encounters. */
        sortTypes = new LinkedList<>();
        sortTypes.add("Insertion Sort");
        sortTypes.add("Selection Sort");
        sortTypes.add("Heap Sort");
        sortTypes.add("Merge Sort");
        sortTypes.add("Quicksort");
        conditionTypes = new LinkedList<>();
        conditionTypes.add("Best");
        conditionTypes.add("Worst");
        answer = new HashMap<>();
        answer.put("Insertion SortBest", 1);
        answer.put("Insertion SortWorst", 3);
        answer.put("Selection SortBest", 3);
        answer.put("Selection SortWorst", 3);
        answer.put("Heap SortBest", 1);
        answer.put("Heap SortWorst", 2);
        answer.put("Merge SortBest", 2);
        answer.put("Merge SortWorst", 2);
        answer.put("QuicksortBest", 2);
        answer.put("QuicksortWorst", 3);
        /*  */
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                encounterPage[x][y] = Tileset.NOTHING;
            }
        }
        while (finalWorldFrame[encounterX][encounterY] != Tileset.FLOOR) {
            encounterX = RandomUtils.uniform(RAND, 0, WIDTH - 1);
            encounterY = RandomUtils.uniform(RAND, 0, HEIGHT - 1);
        }
        finalWorldFrame[encounterX][encounterY] = Tileset.ENCOUNTER;
    }

    private void boxGenerator() {
        /* Set up the box and box destination. */
        int boxX = 0;
        int boxY = 0;
        while (finalWorldFrame[boxX][boxY] != Tileset.FLOOR || finalWorldFrame[boxX + 1][boxY] == Tileset.WALL ||
                finalWorldFrame[boxX - 1][boxY] == Tileset.WALL || finalWorldFrame[boxX][boxY + 1] == Tileset.WALL ||
                finalWorldFrame[boxX][boxY - 1] == Tileset.WALL) {
            boxX = RandomUtils.uniform(RAND, 0, WIDTH - 1);
            boxY = RandomUtils.uniform(RAND, 0, HEIGHT - 1);
        }
        finalWorldFrame[boxX][boxY] = Tileset.BOX;
    }

    private void moveHelper(int x, int y) {
        TETile move = finalWorldFrame[avatarX + x][avatarY + y];
        if (move == Tileset.FLOOR || move == Tileset.ENCOUNTER) {
            finalWorldFrame[avatarX][avatarY] = Tileset.FLOOR;
            avatarX += x;
            avatarY += y;
            finalWorldFrame[avatarX][avatarY] = Tileset.AVATAR;
            if (move == Tileset.ENCOUNTER) {
                encounterHelper();
            }
        } else if (move == Tileset.BOX) {
            if (finalWorldFrame[avatarX + x + x][avatarY + y + y] != Tileset.WALL) {
                finalWorldFrame[avatarX][avatarY] = Tileset.AVATAR;
                avatarX += x;
                avatarY += y;
                finalWorldFrame[avatarX][avatarY] = Tileset.AVATAR;
                finalWorldFrame[avatarX + x][avatarY + y] = Tileset.BOX;
            }
        }
        else {
            lives--;
            if (inputMethod.equals("keyboard")) {
                StdDraw.clear(Color.red);
                StdDraw.show();
                StdDraw.pause(10);
            }
        }
    }

    private void eMoveHelper(int x, int y) {
        if (!(avatarEX + x < 0 || avatarEX + x >= WIDTH || avatarEY + y < 0 || avatarEY + y >= HEIGHT)) {
            encounterPage[avatarEX][avatarEY] = Tileset.NOTHING;
            avatarEX += x;
            avatarEY += y;
            encounterPage[avatarEX][avatarEY] = Tileset.AVATAR;
        }
    }

    private void encounterHelper() {
        STATUS = 2;
        int question = RandomUtils.uniform(RAND, 0, sortTypes.size() - 1);
        int bestOrWorst = RandomUtils.uniform(RAND, 0, 1);
        StdDraw.clear(Color.black);
        avatarEX = WIDTH / 2;
        avatarEY = (int) (HEIGHT * 0.3);
        encounterPage[avatarEX][avatarEY] = Tileset.AVATAR;
        ter.renderFrame(encounterPage, lives);
        StdDraw.text(WIDTH / 2, HEIGHT * 0.8, "What is the " + conditionTypes.get(bestOrWorst) +
                " case runtime of " + sortTypes.get(question) + "?");
        StdDraw.text(WIDTH / 2, HEIGHT * 0.65, "A. N    B. NLogN   C. N^2");
        StdDraw.show();
        while (STATUS == 2) {
            if (StdDraw.hasNextKeyTyped()) {
                keyPress(String.valueOf(StdDraw.nextKeyTyped()));
                StdDraw.clear(Color.black);
                encounterPage[avatarEX][avatarEY] = Tileset.AVATAR;
                ter.renderFrame(encounterPage, lives);
                StdDraw.text(WIDTH / 2, HEIGHT * 0.8, "What is the " + conditionTypes.get(bestOrWorst) +
                        " case runtime of " + sortTypes.get(question) + "?");
                StdDraw.text(WIDTH / 2, HEIGHT * 0.65, "A. N    B. NLogN   C. N^2");
                StdDraw.show();
                if (avatarEX > 33 && avatarEX < 36 && avatarEY > 16 && avatarEY < 20) {
                    scorePage(answer.get(sortTypes.get(question) + conditionTypes.get(bestOrWorst)) == 1);
                } else if (avatarEX > 36 && avatarEX < 42 && avatarEY > 16 && avatarEY < 20) {
                    scorePage(answer.get(sortTypes.get(question) + conditionTypes.get(bestOrWorst)) == 2);
                } else if (avatarEX > 42 && avatarEX < 47 && avatarEY > 16 && avatarEY < 20) {
                    scorePage(answer.get(sortTypes.get(question) + conditionTypes.get(bestOrWorst)) == 3);
                }
            }
            encounterPage[avatarEX][avatarEY] = Tileset.NOTHING;
        }
    }

    public void scorePage(boolean correct) {
        Font font = StdDraw.getFont();
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(fontBig);
        if (correct) {
            lives += 3;
            StdDraw.setPenColor(Color.yellow);
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "HUMANITY RESTORED");
            StdDraw.show();
            StdDraw.pause(1000);
        } else {
            lives -= 2;
            StdDraw.setPenColor(Color.red);
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "HUMANITY LOST");
            int eye = 0;
            while (eye < 50) {
                double x = RandomUtils.gaussian(new Random(),WIDTH / 2 , WIDTH * 0.3);
                double y = RandomUtils.gaussian(new Random(),HEIGHT / 2, HEIGHT * 0.3);
                StdDraw.text(x, y, "＜○＞");
                StdDraw.pause((int) Math.abs(RandomUtils.gaussian(new Random(),20, 5)));
                StdDraw.show();
                eye++;
            }
        }
        StdDraw.setFont(font);
        encounters--;
        STATUS = 1;
    }

    private boolean loadHelper() {
        if (!inputFile.isFile()) {
            if (inputMethod.equals("keyboard")) {
                StdDraw.clear(Color.BLACK);
                StdDraw.setFont(fontBig);
                StdDraw.setPenColor(Color.red);
                StdDraw.text(0.5, 0.55, "Failed to Load:");
                StdDraw.setFont(fontSmall);
                StdDraw.text(0.5, 0.45, "Save files do not exist yet.");
                StdDraw.pause(1000);
                interactWithKeyboard();
            }
            return false;
        }
        return true;
    }

    private void replay() {
        if (loadHelper()) {
            String input = Utils.readContentsAsString(inputFile);
            for (int i = 0; i < input.length(); i++) {
                String in = Character.toString(input.charAt(i));
                keyPress(in);
                if (STATUS == 1) {
                    ter.renderFrame(finalWorldFrame, lives);
                    StdDraw.pause(150);
                }
            }
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "Replay Complete");
            StdDraw.show();
            StdDraw.pause(1000);
        }
    }

    public void drawSeedInputPage(String str) {
        StdDraw.setPenColor(Color.white);
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(fontBig);
        StdDraw.text(0.5, 0.7, "Please input SEED");
        StdDraw.setFont(fontSmall);
        StdDraw.text(0.5, 0.6, "Leave blank for default");
        StdDraw.text(0.5, 0.4, "Note: Press S to finish");
        if (SEED.length() >= 19) {
            SEED = "";
            str = "";
            StdDraw.setPenColor(Color.red);
            StdDraw.text(0.5, 0.3, "Seed too large: input cleared");
        }
        StdDraw.text(0.5, 0.5, str);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        inputMethod = "string";
        for (int i = 0; i < input.length(); i++) {
            keyPress(Character.toString(input.charAt(i)));
        }
        return finalWorldFrame;
    }
}