package agents;

import java.util.Random;

import wumpus.Agent;
import wumpus.Environment.Action;
import wumpus.Player;

/**
 * The fake agent tha implements random actions strategy.
 */
public class RandomAgent implements Agent {
    public boolean debug = true;
    public final Random random = new Random();
    public final Action[] actions = {
            Action.GO_FORWARD,
            Action.GO_FORWARD,
            Action.GO_FORWARD,
            Action.GO_FORWARD,
            Action.TURN_LEFT,
            Action.TURN_RIGHT,
            Action.GRAB,
            Action.SHOOT_ARROW
    };

    /**
     * Sets weather to show the debug messages or not.
     * @param value <tt>true</tt> to display messages
     */
    public void setDebug(boolean value) {
        debug = value;
    }

    /**
     * Prints the player board and debug message.
     * @param player The player instance
     */
    public void beforeAction(Player player) {
        if (debug) {
            System.out.println(player.render());
            System.out.println(player.debug());
        }
    }

    /**
     * Prints the last action taken.
     * @param player The player instance
     */
    public void afterAction(Player player) {
        if (debug) {
            // Players Last action
            System.out.println(player.getLastAction());
            // Show a very happy message
            if (player.isDead()) {
                System.out.println("GAME OVER!");
            }
        }
    }

    /**
     * Implements a strategy that takes any random action.
     * @param player The player instance
     * @return The next action
     */
    public Action getAction(Player player) {
        int x = player.getX();
        int y = player.getY();
        // Feel the perceptions
        boolean bump = player.hasBump();
        boolean breeze = player.hasBreeze();
        boolean stench = player.hasStench();
        boolean scream = player.hasScream();
        boolean glitter = player.hasGlitter();

        if (bump || breeze || stench || scream || glitter) {
            // TODO: Do some action based on the players perception...
        }

        return actions[random.nextInt(actions.length - 1)];
    }
}
