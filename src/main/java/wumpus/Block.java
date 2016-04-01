package wumpus;

import java.util.HashSet;

import wumpus.Environment.Item;

/**
 * Describes a single board block, that holds information on what have in.
 */
public class Block {
    private int x, y, w, h;
    private HashSet<Item> items = new HashSet<Item>();

    /**
     * The Block constructor.
     * @param position The linear position in the board
     * @param width The width of the board
     * @param height The height of the board
     */
    public Block(int position, int width, int height) {
        x = position % width;
        y = position / width;
        w = width;
        h = height;
        clear();
    }

    /**
     * Returns this block linear position at the board.
     * @return The linear index
     */
    public int getIndex() { return x + y * w; }

    /**
     * Returns some block linear position from a 2D position.
     * @return The linear index
     */
    public int getIndex(int x, int y) { return x + y * w; }

    /**
     * Returns the horizontal position of this block at the board.
     * @return The X position
     */
    public int getX() { return x; }

    /**
     * Returns the vertical position of this block at the board.
     * @return The Y position
     */
    public int getY() { return y; }

    /**
     * Returns the blocks linear position that share the same borders.
     * <pre>
     *        N
     *     W    E
     *       S
     * </pre>
     *
     * @return The neighbors array with clockwise order {N, E, S, W}
     */
    public int[] getNeighborhood() {
        int[] neighborhood = {-1, -1, -1, -1};
        // Calculate the coordinates to each direction
        int north = y - 1;
        int south = y + 1;
        int west = x - 1;
        int east = x + 1;
        // Limit the boundaries
        if (north >= 0) neighborhood[0] = getIndex(x, north);
        if (south < h)  neighborhood[2] = getIndex(x, south);
        if (east < w)   neighborhood[1] = getIndex(east, y);
        if (west >= 0)  neighborhood[3] = getIndex(west, y);

        return neighborhood;
    }

    /**
     * Resets all items on this block.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Resets some type of item on this block if has on it.
     * @param item The item to remove
     */
    public void remove(Item item) {
        items.remove(item);
    }

    /**
     * Returns weather this block is empty or not.
     * @return <tt>true</tt> if contains no items
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Returns weather this block contains the item or not.
     * @param item The item to find
     * @return <tt>true</tt> if not contains the given item
     */
    public boolean contains(Item item) {
        return items.contains(item);
    }

    /**
     * Adds an item to this block.
     * @param item The item
     */
    public void setItem(Item item)  {
        items.add(item);
    }
}
