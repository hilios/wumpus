package wumpus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import wumpus.Environment.*;

/**
 * The iteration of plays that the player can take until reaches its end.
 */
public class Runner implements Iterable<Player>, Iterator<Player> {
    private static final int DEFAULT_MAX_ITERATIONS = 200;

    private final World world;
    private final String name;
    private int iterations = 0;

    /**
     * The runner constructor.
     * @param world The world instance.
     */
    public Runner(String name, World world) {
        this.name = name;
        this.world = world;
    }

    /**
     * Returns the iterator that can be user in a loop.
     * @return Itself
     */
    public Iterator<Player> iterator() {
        return this;
    }

    /**
     * Check if the game has ended.
     * @return
     */
    public boolean hasNext() {
        Player player = world.getPlayer();
        return iterations < DEFAULT_MAX_ITERATIONS &&
                player.isAlive() && player.getResult() != Result.WIN;
    }

    /**
     * Get player instance to calculate the next iteration.
     * @return The current player instance
     */
    public Player next() {
        if (!hasNext()) throw new NoSuchElementException();
        iterations++;
        return world.getPlayer();
    }

    /**
     * Operation not supported, throws an error.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get how many iterations have been made so far.
     * @return The amount of iterations
     */
    public int getDepth() {
        return iterations;
    }
}
