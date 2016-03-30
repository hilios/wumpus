package wumpus;

import java.util.ArrayList;

import wumpus.Environment.*;

/**
 *
 */
public class Agent {
    public enum Direction {
        N, E, S, W
    }

    int x, y;
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

    public Direction getDirection() { return direction; }

    /**
     * Returns the current block instance.
     * @return The Block instance
     */
    public Block getBlock() {
        return block;
    }

    /**
     * Returns the list of perceptions sensed from the current block.
     * @return The list of perceptions
     */
    public ArrayList<Perceptions> getPerceptions() {
        return perceptions;
    }

    /**
     * Returns if agent senses or not a bump.
     * @return If has a bump perception
     */
    public boolean hasBump() {
        return perceptions.contains(Perceptions.BUMP);
    }

    /**
     * Returns if agent senses or not a breeze.
     * @return If has a breeze perception
     */
    public boolean hasBreeze() {
        return perceptions.contains(Perceptions.BREEZE);
    }

    /**
     * Returns if agent senses or not a stenche.
     * @return If has a stench perception
     */
    public boolean hasStench() {
        return perceptions.contains(Perceptions.STENCH);
    }
}
