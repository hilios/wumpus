package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import wumpus.Agent;
import wumpus.Environment;
import wumpus.Environment.Action;
import wumpus.Player;
import wumpus.Player.Direction;

/**
 * An Agent that implements a basic estimation strategy.
 */
public class HeuristicAgent implements Agent {
    private int w, h;
    private boolean[][] visited;

    private LinkedList<Action> nextActions = new LinkedList<Action>();

    /**
     * The strategy constructor.
     * @param width The board width
     * @param height  The board height
     */
    public HeuristicAgent(int width, int height) {
        w = width;
        h = height;
        visited = new boolean[w][h];
    }

    /**
     * Prints the player board and debug message.
     * @param player The player instance
     */
    public void beforeAction(Player player) {
        System.out.println(player.render());
        System.out.println(player.debug());
    }

    /**
     * Prints the last action taken.
     * @param player The player instance
     */
    public void afterAction(Player player) {
        // Players Last action
        System.out.println(player.getLastAction());
        // Show a very happy message
        if (player.isDead()) {
            System.out.println("GAME OVER!");
        }
        // Step-by-step
        Environment.trace();
    }

    /**
     * Implements the player artificial intelligence strategy.
     * @param player The player instance
     * @return The next action
     */
    public Action getAction(Player player) {
        // Mark this block has visited
        int x = player.getX();
        int y = player.getY();

        // Set this block as visited
        visited[x][y] = true;

        // Apply actions pools
        if (nextActions.size() > 0) {
            return nextActions.poll();
        }

        // Grab the gold if senses glitter
        if (player.hasGlitter()) return Action.GRAB;

        // Shoot an arrow to every non visited tiles if senses a stench
        if (player.hasStench() && player.hasArrows()) {
            int[][] branches = getNeighbors(x, y);
            for(int[] branch : branches) {
                // Killer instinct
                if (!visited[branch[0]][branch[1]]) {
                    ArrayList<Action> actions = getActionsToShoot(player, branch);
                    nextActions.addAll(actions);
                    return nextActions.poll();
                }
            }
        }

        // Evaluate the cost of neighbor branches
        int currentCost = 999;
        int[] next = {-1, -1};
        int[][] branches = getNeighbors(x, y);
        for (int[] branch : branches) {
            int cost = getCost(player, branch);
            if(cost < currentCost) {
                currentCost = cost;
                next = branch;
            }
            // Debug
            System.out.format("(%d,%d) = %d%n", branch[0], branch[1], currentCost);
        }
        System.out.format("Go to (%d,%d)%n", next[0], next[1]);

        // Execute the action to get to the branch with less cost
        ArrayList<Action> actions = getActionsTo(player, next);
        nextActions.addAll(actions);

        // Auto execute the first action
        return nextActions.poll();
    }

    /**
     * Gets the adjacent tiles of the given coordinates.
     * @param x
     * @param y
     * @return An array of 2D coordinates
     */
    private int[][] getNeighbors(int x, int y) {
        HashMap<Direction, Integer> nodesMap = new HashMap<Direction, Integer>();

        // Calculate the next block
        int north = y - 1;
        int south = y + 1;
        int east = x + 1;
        int west = x - 1;

        // Check if branch is into bounds
        if (north >= 0) nodesMap.put(Direction.N, north);
        if (south < h)  nodesMap.put(Direction.S, south);
        if (east < w)   nodesMap.put(Direction.E, east);
        if (west >= 0)  nodesMap.put(Direction.W, west);

        // Build the branches array
        int branch = 0;
        int[][] nodes = new int[nodesMap.size()][2];
        for (Direction direction : nodesMap.keySet()) {
            switch (direction) {
                case N: nodes[branch] = new int[]{x, north}; break;
                case S: nodes[branch] = new int[]{x, south}; break;
                case E: nodes[branch] = new int[]{east, y}; break;
                case W: nodes[branch] = new int[]{west, y}; break;
            }
            branch++;
        }

        return nodes;
    }

    /**
     * Returns the amount of turns player need to take to get into given position.
     * @param player The player's instance
     * @param to The destination tile
     * @return The number of turns
     */
    private int getTurns(Player player, int[] to) {
        // The current vector
        int[] from = {1, 0};
        switch (player.getDirection()) {
            case N:
                from[0] = 0;
                from[1] = 1;
                break;
            case S:
                from[0] = 0;
                from[1] = -1;
                break;
            case W:
                from[0] = -1;
                from[1] = 0;
                break;
        }
        // The destination vector
        int[] dest = {to[0] - player.getX(), player.getY() - to[1]};
        // The angle between the two vectors
        double dotProduct = from[0] * dest[0] + from[1] * dest[1];
        double lenProduct = Math.hypot(from[0], from[1]) * Math.hypot(dest[0], dest[1]);
        double theta = Math.acos(dotProduct / lenProduct);
        // Inverts when facing backwards
        if (from[1] < 0 || dest[0] < 0) theta *= -1;
        if (from[0] < 0 || dest[0] > 0) theta *= -1;
        // System.out.format("(%d,%d)x(%d,%d) > %.0f = %.0f%n", from[0], from[1], dest[0], dest[1],
        //      Math.toDegrees(theta), turns);
        // Count how many turns
        return (int)(theta / (Math.PI / 2));
    }

    /**
     * Returns the cost for to reach the given branch.
     * @param player The player's instance
     * @param to The destination block coordinates
     * @return The cost estimation tho reach the tile
     */
    private int getCost(Player player, int[] to) {
        // Start with at least one forward
        int sum = 1;
        // If found gold choose the safest path otherwise costs more to return
        if (visited[to[0]][to[1]]) {
            if (player.hasGold()) sum -= 5;
            else sum += 5;
        } else {
            // If senses a breeze avoid unvisited path
            if (player.hasBreeze()) {
                sum += 10;
            }
        }

        // The amount fo turns to take
        int turns = getTurns(player, to);
        sum += Math.abs(turns);

        return sum;
    }

    /**
     * Returns the actions that player must take to reach the given destination.
     * @param player The player's instance
     * @param to The destination tile coordinates
     * @return An array of actions
     */
    private ArrayList<Action> getActionsTo(Player player, int[] to) {
        ArrayList<Action> actions = new ArrayList<Action>();
        int turns = getTurns(player, to);
        for (int i = 0; i < Math.abs(turns); i++) {
            if (turns < 0) actions.add(Action.TURN_RIGHT);
            if (turns > 0) actions.add(Action.TURN_LEFT);

        }
        // Go to the block
        actions.add(Action.GO_FORWARD);

        return actions;
    }


    /**
     * Returns the actions that player must take to reach the given destination.
     * @param player The player's instance
     * @param to The destination tile coordinates
     * @return An array of actions
     */
    private ArrayList<Action> getActionsToShoot(Player player, int[] to) {
        ArrayList<Action> actions = new ArrayList<Action>();
        int turns = getTurns(player, to);
        for (int i = 0; i < Math.abs(turns); i++) {
            if (turns < 0) actions.add(Action.TURN_RIGHT);
            if (turns > 0) actions.add(Action.TURN_LEFT);

        }
        // Go to the block
        actions.add(Action.SHOOT_ARROW);

        return actions;
    }

    private String debug(boolean[][] matrix) {
        StringBuilder output = new StringBuilder();
        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[x].length; y++) {
                // Transpose...
                boolean value = matrix[y][x];
                if (value) {
                    output.append("T");
                } else {
                    output.append("F");
                }
                if (y < matrix[x].length - 1) {
                    output.append(" | ");
                }
            }
            output.append("\n");
        }
        return output.toString();
    }
}