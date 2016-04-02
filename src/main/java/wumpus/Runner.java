package wumpus;

import java.util.Iterator;
import java.util.NoSuchElementException;

import wumpus.Environment.Result;

/**
 * The iteration of plays that the player can take until reaches its end.
 */
public class Runner implements Iterable<Player>, Iterator<Player> {
    private final World world;
    private int iterations = 0;
    private int maxIterations;

    /**
     * The runner constructor.
     * @param world The world instance.
     */
    public Runner(World world) {
        this.world = world;
        this.maxIterations = world.getMaxSteps();
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
        return iterations < maxIterations &&
                player.isAlive() && world.getResult() != Result.WIN;
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
}
