package wumpus;

import java.util.Iterator;
import java.util.Random;
import wumpus.Environment.*;

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

    private final Player player;
    private final Block[] world;

    /**
     * Creates a new world with given dimensions and default dangers.
     * @param width The horizontal constraint of the board
     * @param height The vertical constraint of the board
     * @throws InterruptedException
     * @throws InternalError
     */
    public World(int width, int height) throws InterruptedException, InternalError {
        this(width, height, DEFAULT_WUMPUS, DEFAULT_PITS);
    }

    /**
     * Creates a new world with given dimensions and dangers.
     * @param width The horizontal constraint of the board
     * @param height The vertical constraint of the board
     * @param wumpus The amount of Wumpus in the board.
     * @param pits The amount of pits in the board.
     * @throws InterruptedException
     * @throws InternalError
     */
    public World(int width, int height, int wumpus, int pits) throws InterruptedException,
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
        // Place hunter agent
        player = new Player(this);
        player.setBlock(0, height - 1);
        // Set objectives
        setRandom(Items.GOLD, DEFAULT_GOLD);
        setRandom(Items.WUMPUS, wumpus);
        setRandom(Items.PIT, pits);
    }

    /**
     * Starts playing until game reachs its end.
     * @return
     */
    public Runner start() {
        return new Runner(this);
    }

    /**
     * Sets a random position for the a set of items respecting safe blocks.
     * @param item The item to be place
     * @param times How many items to be placed.
     * @throws Exception
     */
    private void setRandom(Items item, int times) throws InterruptedException {
        Random random = new Random();
        int tries = 0;
        // Set the starting point neighbors as safe
        int[] safeBlocks = player.getBlock().getNeighbors();

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
                            "many times.");
                } else {
                    tries++;
                }
            }
        }
    }

    /**
     * Renders the game board as an ASCII string.
     *
     * Each block is composed by:
     * <pre>
     *     +-----+
     *     |   D |
     *     | H P |
     *     +-----+
     *     D = Danger, P = Perception, H = Hunter
     * </pre>
     *
     * @return The board representation.
     */
    public String render() {
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
                                int[] neighbors = block.getNeighbors();
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
     * Returns the board block at given linear position.
     * @param position The block position
     * @return The block instance
     */
    public Block getPosition(int position) {
        return world[position];
    }

    /**
     * Returns the board block at given 2D position.
     * @param x The horizontal position
     * @param y The vertical position
     * @return The block instance
     */
    public Block getPosition(int x, int y) {
        int i = (x + y * width);
        return world[i];
    }

    /**
     * Returns the agent of this world.
     * @return The agent instance
     */
    public Player getPlayer() { return player; }
}
