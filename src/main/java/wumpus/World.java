package wumpus;

import java.util.Random;
import java.util.regex.Pattern;

import wumpus.Environment.Actions;
import wumpus.Environment.Items;
import wumpus.Environment.Perceptions;

/**
 * The World is a representation of the game board, it handles the position of the peers and the
 * render of it.
 */
public class World {
    private static final int RANDOM_MAX_TRIES = 20;
    private static final int DEFAULT_GOLD = 1;
    private static final int DEFAULT_WUMPUS = 1;
    private static final int DEFAULT_PITS = 2;

    private final int width;
    private final int height;

    private int gold = DEFAULT_GOLD;
    private int pits = DEFAULT_PITS;
    private int wumpus = DEFAULT_WUMPUS;

    private String agentName;
    private final Player player;
    private final Block[] world;

    /**
     * Creates a new world with given dimensions and dangers.
     * @param width The horizontal constraint of the board
     * @param height The vertical constraint of the board
     * @throws InterruptedException
     * @throws InternalError
     */
    public World(int width, int height) throws InterruptedException,
            InternalError {
        if (width == 1 && height == 1) {
            throw new InternalError("The world size must be greater than 1x1.");
        }

        this.width = width;
        this.height = height;
        // Generate the board grid (WxH)
        world = new Block[width * height];
        for (int i = 0; i < width * height; i++) {
            world[i] = new Block(i, width, height);
        }
        // Set the player
        player = new Player(this);
        // Place items at the board
        reset();
    }

    /**
     * Execute an agent that plays the game automatically.
     * @param agent The agent instance
     * @throws InterruptedException
     */
    public void execute(Agent agent) throws InterruptedException {
        agentName = agent.getClass().getName();

        for (Player player : run()) {
            Actions actions = agent.getAction(player);
            player.setAction(actions);
        }
    }

    /**
     * Returns the current agent class name.
     * @return The agent name
     */
    public String getAgentName() {
        return agentName;
    }

    /**
     * Starts playing until game reachs its end.
     * @return The plays iteration
     * @throws InterruptedException
     */
    private Runner run() throws InterruptedException {
        reset();
        return new Runner(this);
    }

    /**
     * Set the number of pits on the board.
     * @param value
     */
    public void setPits(int value) {
        pits = value;
    }

    /**
     * Set the number of Wumpus on the board.
     * @param value
     */
    public void setWumpus(int value) {
        wumpus = value;
    }

    /**
     * Sets a random position for the a set of items respecting safe blocks.
     * @param item The item to be place
     * @param times How many items to be placed.
     * @throws InterruptedException When reaches too many tries
     */
    private void setRandom(Items item, int times) throws InterruptedException {
        Random random = new Random();
        int tries = 0;
        // Set the starting point neighbors as safe
        int[] safeBlocks = player.getBlock().getNeighborhood();

        for(int i = 0; i < times; i++) {
            Block position;
            // Find an empty block to set the item
            while (true) {
                int z = random.nextInt(width * height - 1);
                position = world[z];
                if(position.isEmpty() &&
                        z != safeBlocks[0] && z != safeBlocks[1]  && z != safeBlocks[2]  &&
                        z != safeBlocks[3]) {
                    position.setItem(item);
                    break;
                }
                // Do not loop forever
                if (tries >= RANDOM_MAX_TRIES) {
                    throw new InterruptedException("Cannot set a random position for item after " +
                            "many tries, increase the world dimensions.");
                } else {
                    tries++;
                }
            }
        }
    }

    /**
     * Returns the index from a given 2D position.
     * @param x The horizontal position
     * @param y The vertical position
     * @return The index
     */
    public int getIndex(int x, int y) {
        return (x + y * width);
    }

    /**
     * Returns the board block at given linear position.
     * @param index The block position
     * @return The block instance
     */
    public Block getPosition(int index) {
        return world[index];
    }

    /**
     * Returns the board block at given 2D position.
     * @param x The horizontal position
     * @param y The vertical position
     * @return The block instance
     */
    public Block getPosition(int x, int y) {
        int i = getIndex(x, y);
        return world[i];
    }

    /**
     * Returns the player set at this world.
     * @return The player instance
     */
    public Player getPlayer() { return player; }

    /**
     * Resets the board with custom dangers.
     * @throws InterruptedException
     */
    public void reset() throws InterruptedException {
        // Reset all blocks
        for (int i = 0; i < world.length; i++) {
            world[i].reset();
        }
        // Reset the player agent
        player.setBlock(0, height - 1);
        player.reset();
        // Set the dangers
        setRandom(Items.WUMPUS, wumpus);
        setRandom(Items.PIT, pits);
        // Set the objective
        setRandom(Items.GOLD, gold);
    }

    /**
     * Renders a simplified version of the game board as an ASCII string.
     * Each block is has only the hunter:
     * <pre>
     *     +---+
     *     | H |
     *     +---+
     * </pre>
     *
     * @return The board representation
     */
    public String render() {
        StringBuilder render = new StringBuilder();

        for(int y = 0; y < height; y++) {
            for(int z = 0; z < 2; z++) {
                for (int x = 0; x < width; x++) {
                    switch (z) {
                        case 0:
                            if (x == 0) render.append("+");
                            render.append("---+");
                            break;
                        default:
                            Block block = getPosition(x, y);
                            String line = " 1 |";
                            if (block.contains(Items.HUNTER)) {
                                line = line.replace("1", Environment.getIcon(player));
                            }
                            // Erase any non-replaced items
                            line = line.replace("1", " ");
                            // Draw
                            if (x == 0) render.append("|");
                            render.append(line);
                    }
                }
                render.append("\n");
            }
        }
        for (int i = 0; i < width; i++) {
            if (i == 0) render.append("+");
            render.append("---+");
        }
        return render.toString();
    }

    /**
     * Renders the full game board as an ASCII string.
     * Each block is composed by:
     * <pre>
     *     +-----+
     *     |   D |
     *     | H P |
     *     +-----+
     *     D = Danger, P = Perception, H = Hunter
     * </pre>
     *
     * @return The board representation
     */
    public String renderAll() {
        StringBuilder render = new StringBuilder();

        for(int y = 0; y < height; y++) {
            for(int z = 0; z < 3; z++) {
                for (int x = 0; x < width; x++) {
                    switch (z) {
                        case 0:
                            if (x == 0) render.append("+");
                            render.append("-----+");
                            break;
                        default:
                            Block block = getPosition(x, y);
                            String line = " 1 2 |";
                            if (z == 1) {
                                // Renders the second line
                                if (block.contains(Items.WUMPUS)) {
                                    line = line.replace("2", Environment.getIcon(Items.WUMPUS));
                                }
                                if (block.contains(Items.PIT)) {
                                    line = line.replace("2", Environment.getIcon(Items.PIT));
                                }
                                if (block.contains(Items.GOLD)) {
                                    line = line.replace("2", Environment.getIcon(Items.GOLD));
                                }
                            } else {
                                if (block.contains(Items.HUNTER)) {
                                    line = line.replace("1", Environment.getIcon(player));
                                }
                                if (block.contains(Items.GOLD)) {
                                    line = line.replace("2",
                                            Environment.getIcon(Perceptions.GLITTER));
                                }
                                // Mark this block if some of their neighbor has some danger
                                int[] neighbors = block.getNeighborhood();
                                for (int s = 0; s < neighbors.length; s++) {
                                    if (neighbors[s] == -1) continue;
                                    Block neighbor = getPosition(neighbors[s]);
                                    if (neighbor.contains(Items.WUMPUS)) {
                                        line = line.replace("2",
                                                Environment.getIcon(Perceptions.STENCH));
                                    }
                                    if (neighbor.contains(Items.PIT)) {
                                        line = line.replace("2",
                                                Environment.getIcon(Perceptions.BREEZE));
                                    }
                                }
                            }
                            // Erase any non-replaced items
                            line = line.replace("1", " ").replace("2", " ");
                            // Draw
                            if (x == 0) render.append("|");
                            render.append(line);
                    }
                }
                render.append("\n");
            }
        }
        for (int i = 0; i < width; i++) {
            if (i == 0) render.append("+");
            render.append("-----+");
        }
        return render.toString();
    }

    /**
     * Replaces all string occurrences of a char padding if needed.
     * @param input The input string
     * @param oldChar The string to replace
     * @param newChar The replace string
     * @return The replaced input string
     */
    private String padReplace(String input, String oldChar, String newChar) {
        Pattern pattern = Pattern.compile(oldChar + "{" + newChar.length() + "}");
        return pattern.matcher(input).replaceFirst(newChar).replace(oldChar, " ");
    }

    /**
     * Renders the score table as a ASCII string.
     * @return The score table
     */
    public String renderScore() {
        String scoreTable =
                "+----------------------------+\n" +
                "| Outcome | Score    | Steps |\n" +
                "| ------- | -------- | ----- |\n" +
                "| ####### | &&&&&&&& | @@@@@ |\n" +
                "+----------------------------+\n";
        scoreTable = padReplace(scoreTable, "#", player.getResult().toString());
        scoreTable = padReplace(scoreTable, "&", Integer.toString(player.getScore()));
        scoreTable = padReplace(scoreTable, "@", Integer.toString(player.getActions().size()));
        return scoreTable;
    }


    /**
     * Returns the game title as a ASCII string.
     * @return The game title
     */
    public String renderTitle() {
        return  " _       __                                    _       __           __    __\n" +
                "| |     / /_  ______ ___  ____  __  _______   | |     / /___  _____/ /___/ /\n" +
                "| | /| / / / / / __ `__ \\/ __ \\/ / / / ___/   | | /| / / __ \\/ ___/ / __  /\n" +
                "| |/ |/ / /_/ / / / / / / /_/ / /_/ (__  )    | |/ |/ / /_/ / /  / / /_/ /  \n" +
                "|__/|__/\\__,_/_/ /_/ /_/ .___/\\__,_/____/     |__/|__/\\____/_/  /_/\\__,_/\n" +
                "                      /_/";
    }
}
