import java.util.Random;

import wumpus.Agent;
import wumpus.Environment.Perceptions;
import wumpus.Environment.Actions;
import wumpus.Player;

/**
 * The fake agent does random actions.
 */
public class RandomAgent implements Agent {
    public Random random = new Random();
    public Actions[] actions = {
            Actions.GO_FORWARD,
            Actions.GO_FORWARD,
            Actions.GO_FORWARD,
            Actions.GO_FORWARD,
            Actions.TURN_LEFT,
            Actions.TURN_RIGHT,
            Actions.GRAB,
            Actions.SHOOT
    };

    public String getName() {
        return "Random Agent";
    }

    public Actions getAction(Player player) {
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

        Actions nextAction = actions[random.nextInt(actions.length - 1)];
        // Print the board
        StringBuilder perceptions = new StringBuilder();
        for (Perceptions perception : player.getPerceptions()) {
            perceptions.append(" > " + perception.toString());
        }

        System.out.println(player.render());
        System.out.println("Block: (" + x + "," + y + ")");
        System.out.println("Perceptions:" + perceptions.toString());
        System.out.println("Action: " + nextAction);
        System.out.println("");

        return nextAction;
    }
}
