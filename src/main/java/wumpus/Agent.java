package wumpus;

import wumpus.Environment.Action;

/**
 * The Agent that controls the player for each play, to be used to implement custom AI strategies.
 */
public interface Agent {
    /**
     * Executes every play to determine the next action.
     * @param player The player instance
     * @return The action to execute
     */
    public Action getAction(Player player);
}
