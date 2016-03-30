package wumpus;

/**
 * Represents the world environment.
 */
public class Environment {
    public enum Items {
        WUMPUS, PIT, HUNTER, GOLD
    }

    public enum Perceptions {
        SCREAM, STENCH, BREEZE, GLITTER, BUMP, NOTHING, NO_ARROWS
    }

    public enum Actions {
        GO_FORWARD, TURN_LEFT, TURN_RIGHT, GRAB, SHOOT, NO_OP
    }

    public static String getIcon(Items item) {
        switch (item) {
            case WUMPUS: return "W";
            case HUNTER: return "H";
            case PIT: return "P";
            case GOLD: return "$";
        }
        return " ";
    }

    public static String getIcon(Perceptions perception) {
        switch (perception) {
            case STENCH: return "~";
            case BREEZE: return "≈";
            case GLITTER: return "*";
        }
        return " ";
    }
}
