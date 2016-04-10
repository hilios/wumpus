package wumpus;

import java.util.HashSet;

import wumpus.Environment.Element;

/**
 * Describes a single board block, that holds information on what have in.
 */
public class Tile {
    private int x, y, w, h;
    private HashSet<Environment.Element> elements = new HashSet<Element>();

    /**
     * The Tile constructor.
     * @param position The linear position in the board
     * @param width The width of the board
     * @param height The height of the board
     */
    public Tile(int position, int width, int height) {
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
    public int[] getNeighbors() {
        int[] neighbors = {-1, -1, -1, -1};
        // Calculate the coordinates to each direction
        int north = y - 1;
        int south = y + 1;
        int west = x - 1;
        int east = x + 1;
        // Limit the boundaries
        if (north >= 0) neighbors[0] = getIndex(x, north);
        if (south < h)  neighbors[2] = getIndex(x, south);
        if (east < w)   neighbors[1] = getIndex(east, y);
        if (west >= 0)  neighbors[3] = getIndex(west, y);

        return neighbors;
    }

    /**
     * Resets all elements on this block.
     */
    public void clear() {
        elements.clear();
    }

    /**
     * Resets some type of item on this block if has on it.
     * @param item The item to remove
     */
    public void remove(Environment.Element item) {
        elements.remove(item);
    }

    /**
     * Returns weather this block is empty or not.
     * @return <tt>true</tt> if contains no elements
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * Returns weather this block contains the element or not.
     * @param element The element to find
     * @return <tt>true</tt> if not contains the given element
     */
    public boolean contains(Element element) {
        return elements.contains(element);
    }

    /**
     * Adds an element to this block.
     * @param element The element
     */
    public void setItem(Element element)  {
        elements.add(element);
    }
}
