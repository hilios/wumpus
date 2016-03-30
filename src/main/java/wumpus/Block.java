package wumpus;

import java.util.ArrayList;

import wumpus.Environment.*;

/**
 *
 */
public class Block {
    int x, y, w, h, boundry;
    ArrayList<Items> items = new ArrayList<Items>();

    public Block(int position, int width, int height) {
        x = position % width;
        y = position / width;
        w = width;
        h = height;
        boundry = w * h;
        reset();
    }

    public int getPosition() { return x + y * w; }
    public int getPosition(int x, int y) { return x + y * w; }

    public int getX() { return x; }
    public int getY() { return y; }

    public int[] getNeighbors() {
        int[] neighbors = {-1, -1, -1, 1};

        int north = y - 1;
        int south = y + 1;
        int west = x - 1;
        int east = x + 1;

        if (north >= 0) neighbors[0] = getPosition(x, north);
        if (south < h) neighbors[1] = getPosition(x, south);
        if (west >= 0) neighbors[2] = getPosition(west, y);
        if (east < w) neighbors[2] = getPosition(east, y);

        return neighbors;
    }

    public void reset() {
        items.clear();
    }

    public void reset(Items item) {
        if (items.contains(item)) {
            items.remove(item);
        }
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean contains(Items item) {
        return items.contains(item);
    }

    public boolean hasDanger() {
        return items.contains(Items.WUMPUS) || items.contains(Items.PIT);
    }

    public void setItem(Items item)  {
        items.add(item);
    }
}
