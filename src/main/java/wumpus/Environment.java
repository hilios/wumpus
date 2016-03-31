package wumpus;

import java.util.ArrayList;

import static wumpus.Environment.Actions.GO_FORWARD;

/**
 * Represents the world environment.
 */
public class Environment {
    /**
     * The items that can be found at the world blocks.
     */
    public enum Items {
        WUMPUS, PIT, HUNTER, GOLD
    }

    /**
     * The perceptions that can be sensed by the player.
     */
    public enum Perceptions {
        SCREAM, STENCH, BREEZE, GLITTER, BUMP, NOTHING, NO_ARROWS
    }

    /**
     * The actions that the player can take.
     */
    public enum Actions {
        GO_FORWARD, TURN_LEFT, TURN_RIGHT, GRAB, SHOOT, NO_OP
    }

    /**
     * The final outcome of the game.
     */
    public enum Result {
        WIN, LOOSE
    }

    /**
     * Returns the player score based on his list of actions and current state.
     * @param player The player instance
     * @return The current score
     */
    public static int getScore(Player player) {
        int sum = 0;
        // Score if have deceased
        if (player.isDead()) sum += -1000;
        // Score if have picked the gold
        if (player.hasGold()) sum += +1000;
        // Calculate the score for each action
        for(Actions action : player.getActions()) {
            switch (action) {
                case GO_FORWARD:
                case TURN_LEFT:
                case TURN_RIGHT:
                case GRAB:
                    sum += -1;
                    break;
                case SHOOT:
                    sum += -10;
                    break;
            }
        }
        return sum;
    }

    /**
     * Returns the icon for a environment item.
     * @param item The item
     * @return The icon
     */
    public static String getIcon(Items item) {
        switch (item) {
            case WUMPUS: return "W";
            case HUNTER: return "H";
            case PIT: return "P";
            case GOLD: return "$";
        }
        return " ";
    }

    /**
     * Returns the icon for a perceptions.
     * @param perception The perception
     * @return The icon
     */
    public static String getIcon(Perceptions perception) {
        switch (perception) {
            case GLITTER: return "*";
            case STENCH: return "~";
            case BREEZE: return "≈";
        }
        return " ";
    }

    /**
     * Return the icon of the player based in its direction o if its dead.
     * @param player The player instance
     * @return The icon
     */
    public static String getIcon(Player player) {
        if (player.isDead()) return "†";

        switch (player.getDirection()) {
            case N: return "↑";
            case E: return "→";
            case S: return "↓";
            case W: return "←";
        }

        return "H";
    }
}
