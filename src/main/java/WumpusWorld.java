import java.util.Random;

/**
 * Created by hilios on 28/03/16.
 */
public class WumpusWorld {
    int width;
    int height;

    Block agent;
    Block[] world;

    public enum Items {
        WUMPUS, PIT, HUNTER, GOLD
    }

    public enum Senses {
        SCREAM, STENCH, BREESE, GLITTER, BUMP
    }

    public enum Actions {
        GO_FORWARD, TURN_LEFT, TURN_RIGHT, GRAB, SHOOT, NO_OP
    }

    public WumpusWorld(int width, int height) {
        this.width = width;
        this.height = height;

        // Generate the board grid (WxH)
        world = new Block[width * height];
        for (int i = 0; i < width * height; i++) {
            world[i] = new Block(i, width, height);
        }
        // Hunter at the initial position
        agent = getPosition(0, height - 1);
        agent.setItem(Items.HUNTER);
        // Set objectives
        setRandom(Items.GOLD, 1);
        setRandom(Items.WUMPUS, 1);
        setRandom(Items.PIT, 3);
    }

    protected void setRandom(Items item, int times) {
        Random random = new Random();

        // Set the starting point neighbors as safe
        int[] safeBlocks = startBlock.getNeighbors();

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
            }
        }
    }

    /**
     * Renders the game board as an ASCII string.
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
                            String line = " x y |";
                            //
                            if (z == 1) {
                                if (block.isHere(Items.WUMPUS)) {
                                    line = line.replace("y", getIcon(Items.WUMPUS));
                                }
                                if (block.isHere(Items.PIT)) {
                                    line = line.replace("y", getIcon(Items.PIT));
                                }
                                if (block.isHere(Items.GOLD)) {
                                    line = line.replace("y", getIcon(Items.GOLD));
                                }
                                // Mark this block if some of their neighbor has some danger
                                int[] neighbors = block.getNeighbors();
                                for (int s = 0; s < neighbors.length; s++) {
                                    if (neighbors[s] == -1) continue;
                                    Block neighbor = getPosition(neighbors[s]);
                                    if (neighbor.hasDanger()) {
                                        line = line.replace("x", "~");
                                        break;
                                    }
                                }
                            } else {
                                if (block.isHere(Items.HUNTER)) {
                                    line = line.replace("x", getIcon(Items.HUNTER));
                                }
                            }

                            line = line.replace("x", " ").replace("y", " ");
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

    public String getIcon(Items item) {
        switch (item) {
            case WUMPUS: return "W";
            case HUNTER: return "H";
            case PIT: return "P";
            case GOLD: return "$";
        }
        return " ";
    }

    public Block getPosition(int z) {
        return world[z];
    }

    public Block getPosition(int x, int y) {
        int i = (x + y * width);
        return world[i];
    }
}
