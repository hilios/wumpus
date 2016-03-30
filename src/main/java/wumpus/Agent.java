package wumpus;

import java.util.ArrayList;

import wumpus.Environment.*;

/**
 * The Agent represents a player of the game that will interact with the world and modify it.
 */
public class Agent {
    public enum Direction {
        N, E, S, W
    }

    int x, y;
    int arrows = 3;
    World world;

    Block block;
    ArrayList<Perceptions> perceptions = new ArrayList<Perceptions>();
    Direction direction = Direction.N;

    /**
     * Creates a new Agent for the given World;
     * @param world
     */
    public Agent(World world) {
        this.world = world;
    }

    /**
     * Set the current block of the agent, un-setting the last one and recalculating all perceptions
     * sensed from the new block.
     * @param x The horizontal position at the board
     * @param y The vertical position at the board
     */
    public void setBlock(int x, int y) {
        // Remove the Hunter from the
        if (block != null) {
            block.reset(Items.HUNTER);
        }
        block = world.getPosition(x, y);
        block.setItem(Items.HUNTER);
        // Reset senses
        perceptions.clear();
        // Senses in the current block
        if (block.contains(Items.GOLD)) {
            perceptions.add(Perceptions.GLITTER);
        }
        // Get the neighbors and find the senses
        int[] neighbors = block.getNeighbors();
        for (int i = 0; i < neighbors.length; i++) {
            if (neighbors[i] > -1) {
                Block neighbor = world.getPosition(neighbors[i]);
                // Sense a breeze when near a pit
                if (neighbor.contains(Items.PIT)) {
                    perceptions.add(Perceptions.BREEZE);
                }
                // Sense a stench when near a Wumpus
                if (neighbor.contains(Items.WUMPUS)) {
                    perceptions.add(Perceptions.STENCH);
                }
            } else {
                // Sense bumps
                if (i == 0 && direction == Direction.N || i == 1 && direction == Direction.E ||
                        i == 2 && direction == Direction.S || i == 3 && direction == Direction.W) {
                    perceptions.add(Perceptions.BUMP);
                }
            }
        }
    }

    /**
     * Returns the current direction of the agent.
     * @return The direction
     */
    public Direction getDirection() { return direction; }

    /**
     * Returns the current block instance.
     * @return The Block instance
     */
    public Block getBlock() {
        return block;
    }

    /**
     * Shoots an arrow and returns the perception of the action.
     * @return The perception of the action, either a Perceptions.SCREAM or Perceptions.NOTHING
     */
    public Perceptions shootArrow() {
        if (arrows > 0) {
            arrows--;
            int[] neighbors = getBlock().getNeighbors();
            // Select the right neighbor to shoot
            Block neighbor = null;
            switch (direction) {
                case N:
                    if (neighbors[0] > -1) neighbor = world.getPosition(neighbors[0]);
                    break;
                case E:
                    if (neighbors[1] > -1) neighbor = world.getPosition(neighbors[1]);
                    break;
                case S:
                    if (neighbors[2] > -1) neighbor = world.getPosition(neighbors[2]);
                    break;
                case W:
                    if (neighbors[3] > -1) neighbor = world.getPosition(neighbors[3]);
                    break;
            }
            // Hear a scream
            if (neighbor != null && neighbor.contains(Items.WUMPUS)) {
                // Add the Scream to the current perception
                perceptions.add(Perceptions.SCREAM);
                return Perceptions.SCREAM;
            }
        }
        return Perceptions.NOTHING;
    }

    /**
     * Returns the list of perceptions sensed from the current block.
     * @return The list of perceptions
     */
    public ArrayList<Perceptions> getPerceptions() {
        return perceptions;
    }

    /**
     * Returns if agent feels or not a bump.
     * @return If has a bump perception
     */
    public boolean hasBump() {
        return perceptions.contains(Perceptions.BUMP);
    }

    /**
     * Returns if agent feels or not a breeze.
     * @return If has a breeze perception
     */
    public boolean hasBreeze() {
        return perceptions.contains(Perceptions.BREEZE);
    }

    /**
     * Returns if agent feels or not a steche.
     * @return If has a stench perception
     */
    public boolean hasStench() {
        return perceptions.contains(Perceptions.STENCH);
    }

    /**
     * Returns if agent hear or not a scream.
     * @return If has a stench perception
     */
    public boolean hasScream() {
        return perceptions.contains(Perceptions.SCREAM);
    }
}
