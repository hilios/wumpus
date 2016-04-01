package agents;

import java.util.Random;

import wumpus.Agent;
import wumpus.Environment;
import wumpus.Environment.Action;
import wumpus.Player;

/**
 * The fake agent tha implements random actions strategy.
 */
public class RandomAgent implements Agent {
    public Random random = new Random();
    public Action[] actions = {
            Action.GO_FORWARD,
            Action.GO_FORWARD,
            Action.GO_FORWARD,
            Action.GO_FORWARD,
            Action.TURN_LEFT,
            Action.TURN_RIGHT,
            Action.GRAB,
            Action.SHOOT
    };

    public void beforeAction(Player player) {
        // Do nothing
    }

    public void afterAction(Player player) {
        System.out.println(player.render());
        System.out.println(player.debug());
    }

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
