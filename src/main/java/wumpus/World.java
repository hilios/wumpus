package wumpus;

import java.util.HashMap;
import java.util.Random;

import wumpus.Environment.Action;
import wumpus.Environment.Item;
import wumpus.Environment.Perception;

/**
 * The World is a representation of the game board, it handles the position of the peers and the
 * render of it.
 */
public class World {
    private static final int DEFAULT_MAX_STEPS = 200;
    private static final int RANDOM_MAX_TRIES = 20;
    private static final int DEFAULT_GOLD = 1;
    private static final int DEFAULT_WUMPUS = 1;
    private static final int DEFAULT_PITS = 2;

    private final int width;
    private final int height;
    private final int startPosition;

    private int gold = DEFAULT_GOLD;
    private int pits = DEFAULT_PITS;
    private int wumpus = DEFAULT_WUMPUS;
    private int maxSteps = DEFAULT_MAX_STEPS;

    private boolean randomize = true;
    private HashMap<Integer, Item> items = new HashMap<Integer, Item>();

    private String agentName;
    private final Player player;
    private final Tile[] tiles;

    /**
     * Creates a new world with given dimensions.
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
        // Generate the board matrix (WxH)
        tiles = new Tile[width * height];
        for (int i = 0; i < width * height; i++) {
            tiles[i] = new Tile(i, width, height);
        }
        // Saves the start position to check the objective
        startPosition = getIndex(0, height - 1);
        // Set the player
        player = new Player(this);
    }

    /**
     * Execute an agent that plays the game automatically.
     * @param agent The agent instance
     * @throws InterruptedException
     */
    public void execute(Agent agent) throws InterruptedException {
        agentName = agent.getClass().getName();

        for (Player player : run()) {
            agent.beforeAction(player);
            Action actions = agent.getAction(player);
            player.setAction(actions);
            agent.afterAction(player);
        }
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
     * Returns the current agent class name.
     * @return The agent name
     */
    public String getAgentName() {
        return agentName;
    }

    /**
     * Returns the maximum steps the player can make before ending the game.
     * @return The max steps allowed
     */
    public int getMaxSteps() {
        return maxSteps;
    }

    /**
     * Sets the maximus steps to finish the game.
     * @param value
     */
    public void setMaxSteps(int value) {
        maxSteps = value;
    }

    /**
     * Set the number of pits on the board.
     * @param value
     */
    public void setPits(int value) {
        pits = value;
    }

    /**
     * Sets a pit at given coordinate.
     * @param x The horizontal coordinate
     * @param y The vertical coordinate
     */
    public void setPit(int x, int y) {
        setItem(Item.PIT, x, y);
    }

    /**
     * Set the number of Wumpus on the board.
     * @param value
     */
    public void setWumpus(int value) {
        wumpus = value;
    }

    /**
     * Sets a Wumpus at given coordinate.
     * @param x The horizontal position
     * @param y The vertical position
     */
    public void setWumpus(int x, int y) {
        setItem(Item.WUMPUS, x, y);
    }

    /**
     * Sets the Gold at given coordinate.
     * @param x The horizontal position
     * @param y The vertical position
     */
    public void setGold(int x, int y) {
        setItem(Item.GOLD, x, y);
    }

    /**
     * Sets the item at given coordinates and saves it for later retrieval.
     * @param item The item to plate
     * @param x The horizontal position
     * @param y The vertical position
     */
    private void setItem(Item item, int x, int y) {
        Tile tile = getPosition(x, y);
        if (tile.isEmpty()) {
            tile.setItem(item);
        } else {
            throw new InternalError("Tile is not empty!");
        }
        // Saves the items position for later retrieval
        items.put(tile.getIndex(), item);
        // Turn off randomization
        randomize = false;
    }

    /**
     * Sets a random position for the a set of items respecting safe blocks.
     * @param item The item to be place
     * @param times How many items to be placed.
     * @throws InterruptedException When reaches too many tries
     */
    private void setRandom(Item item, int times) throws InterruptedException {
        Random random = new Random();
        int tries = 0;
        // Set the starting point neighbors as safe
        int[] safeBlocks = player.getTile().getNeighbors();

        for(int i = 0; i < times; i++) {
            Tile position;
            // Find an empty block to set the item
            while (true) {
                int z = random.nextInt(width * height - 1);
                position = tiles[z];
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
    public Tile getPosition(int index) {
        return tiles[index];
    }

    /**
     * Returns the board block at given 2D position.
     * @param x The horizontal position
     * @param y The vertical position
     * @return The block instance
     */
    public Tile getPosition(int x, int y) {
        int i = getIndex(x, y);
        return tiles[i];
    }

    /**
     * Returns the current player.
     * @return The player instance
     */
    public Player getPlayer() { return player; }

    /**
     * Returns the board width.
     * @return The width
     */
    public int getWidth() { return width; }

    /**
     * Returns the board height.
     * @return The height
     */
    public int getHeight() { return height; }


    /**
     * Returns if the player have win or loose the game.
     * @return The outcome of the game
     */
    public Environment.Result getResult() {
        if (player.isAlive() && player.hasGold() && player.getTile().getIndex() == startPosition) {
            return Environment.Result.WIN;
        }
        return Environment.Result.LOOSE;
    }

    /**
     * Resets the board.
     * @throws InterruptedException
     */
    public void reset() throws InterruptedException {
        // Reset all blocks
        for (int i = 0; i < tiles.length; i++) {
            tiles[i].clear();
        }
        // Reset the player agent
        player.setTile(startPosition);
        player.reset();
        // Set the dangers
        if (randomize) {
            setRandom(Item.WUMPUS, wumpus);
            setRandom(Item.PIT, pits);
            // Set the objective
            setRandom(Item.GOLD, gold);
        } else {
            for (int index : items.keySet()) {
                Tile tile = getPosition(index);
                tile.setItem(items.get(index));
            }
        }
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
                            Tile tile = getPosition(x, y);
                            String line = " 1 |";
                            if (tile.contains(Item.HUNTER)) {
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
                            Tile tile = getPosition(x, y);
                            String line = " 1 2 |";
                            if (z == 1) {
                                // Renders the second line
                                if (tile.contains(Item.WUMPUS)) {
                                    line = line.replace("2", Environment.getIcon(Item.WUMPUS));
                                }
                                if (tile.contains(Item.PIT)) {
                                    line = line.replace("2", Environment.getIcon(Item.PIT));
                                }
                                if (tile.contains(Item.GOLD)) {
                                    line = line.replace("2", Environment.getIcon(Item.GOLD));
                                }
                            } else {
                                if (tile.contains(Item.HUNTER)) {
                                    line = line.replace("1", Environment.getIcon(player));
                                }
                                if (tile.contains(Item.GOLD)) {
                                    line = line.replace("2",
                                            Environment.getIcon(Perception.GLITTER));
                                }
                                // Mark this tile if some of their neighbor has some danger
                                int[] neighbors = tile.getNeighbors();
                                for (int s = 0; s < neighbors.length; s++) {
                                    if (neighbors[s] == -1) continue;
                                    Tile neighbor = getPosition(neighbors[s]);
                                    if (neighbor.contains(Item.WUMPUS)) {
                                        line = line.replace("2",
                                                Environment.getIcon(Perception.STENCH));
                                    }
                                    if (neighbor.contains(Item.PIT)) {
                                        line = line.replace("2",
                                                Environment.getIcon(Perception.BREEZE));
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
     * Renders the score table as a ASCII string.
     * @return The score table
     */
    public String renderScore() {
        String scoreTable = String.format(
                "+----------------------------+%n" +
                "| Outcome | Score    | Steps |%n" +
                "| ------- | -------- | ----- |%n" +
                "| %-7s | %8d | %5d |%n" +
                "+----------------------------+%n",
                getResult().toString(), player.getScore(), player.getActions().size()
            );

        return scoreTable;
    }
}
