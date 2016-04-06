package wumpus;

import java.util.ArrayList;

import wumpus.Environment.Action;
import wumpus.Environment.Item;
import wumpus.Environment.Perception;

/**
 * The Player represents an interactive agent of the game that will manipulate the world and modify
 * it until reaches the end.
 */
public class Player extends Object {
    public enum Direction {
        N, E, S, W
    }

    private final World world;
    private int x, y;

    private Tile tile;

    private ArrayList<Perception> perceptions = new ArrayList<Perception>();
    private ArrayList<Action> actions = new ArrayList<Action>();
    private Direction direction = Direction.E;
    private boolean completed = false;
    private boolean alive = true;
    private boolean gold = false;
    private int arrows = 3;

    /**
     * Creates a new Player for the given world.
     * @param world The world instance that this player belongs
     */
    public Player(World world) {
        this.world = world;
    }

    /**
     * Get the horizontal position of the player at the board.
     * @return The X position
     */
    public int getX() {
        return tile.getX();
    }

    /**
     * Get the vertical position of the player at the board.
     * @return The Y position
     */
    public int getY() {
        return tile.getY();
    }

    /**
     * Resets the player state.
     */
    protected void reset() {
        arrows = 3;
        gold = false;
        completed = false;
        direction = Direction.E;
        actions.clear();
    }

    /**
     * Returns the current tile instance.
     * @return The Tile instance
     */
    protected Tile getTile() {
        return tile;
    }

    /**
     * Set the current tile of the agent, un-setting the last one and recalculating all perceptions
     * sensed from the new tile.
     */
    protected void setTile(int index) {
        // Remove the Hunter from the
        if (tile != null) {
            tile.remove(Item.HUNTER);
        }
        tile = world.getPosition(index);
        tile.setItem(Item.HUNTER);
        // 2D coordinates
        x = tile.getX();
        y = tile.getY();
        // Check if player is still alive
        alive = !(tile.contains(Item.WUMPUS) || tile.contains(Item.PIT));
    }

    /**
     * Returns the current direction of the agent.
     * @return The direction
     */
    public Direction getDirection() { return direction; }

    /**
     * Shoots an arrow and returns the perception of the action.
     * @return The perception of the action, either a Perception.SCREAM or Perception.NOTHING
     */
    public Perception shootArrow() {
        if (arrows > 0) {
            arrows--;
            int[] neighbors = getTile().getNeighbors();
            // Select the right neighbor to shoot
            Tile neighbor = null;
            switch (direction) {
                case N:
                    if (neighbors[0] > -1) neighbor = world.getPosition(neighbors[0]);
                    break;
                case E:
                    if (neighbors[1] > -1) neighbor = world.getPosition(neighbors[1]);
                    break;
                case S:
                    if (neighbors[2] > -1) neighbor = world.getPosition(neighbors[2]);
                    break;
                case W:
                    if (neighbors[3] > -1) neighbor = world.getPosition(neighbors[3]);
                    break;
            }
            // Hear a scream after if killed Wumpus
            if (neighbor != null && neighbor.contains(Item.WUMPUS)) {
                neighbor.remove(Item.WUMPUS);
                // Add the Scream to the current perception
                return Perception.SCREAM;
            }
            // Nothing happens
            return Perception.SHOT_MISSED;
        } else {
            return Perception.NO_ARROWS;
        }
    }

    /**
     * Interacts with the world executing an action.
     * @param action The action to take
     */
    protected void setAction(Action action) {
        actions.add(action);
        // Execute the action
        switch (action) {
            case GO_FORWARD:
                int[] neighbors = tile.getNeighbors();
                switch (direction) {
                    case N:
                        if (neighbors[0] > -1) setTile(neighbors[0]);
                        break;
                    case E:
                        if (neighbors[1] > -1) setTile(neighbors[1]);
                        break;
                    case S:
                        if (neighbors[2] > -1) setTile(neighbors[2]);
                        break;
                    case W:

                        if (neighbors[3] > -1) setTile(neighbors[3]);
                        break;
                }
                break;
            case TURN_LEFT:
                // Mover counter clockwise
                switch (direction) {
                    case N: direction = Direction.W; break;
                    case W: direction = Direction.S; break;
                    case S: direction = Direction.E; break;
                    case E: direction = Direction.N; break;
                }
                break;
            case TURN_RIGHT:
                // Mover clockwise
                switch (direction) {
                    case N: direction = Direction.E; break;
                    case E: direction = Direction.S; break;
                    case S: direction = Direction.W; break;
                    case W: direction = Direction.N; break;
                }
                break;
            case GRAB:
                // If tile has gold store and remove from the tile
                if (tile.contains(Item.GOLD)) {
                    tile.remove(Item.GOLD);
                    gold = true;
                }
                break;
            case SHOOT_ARROW:
                Perception perception = shootArrow();
                setPerceptions(perception);
                return;
        }
        // Reprocess all events
        setPerceptions();
    }

    /**
     * Returns the player actions so far.
     * @return The list of actions
     */
    public ArrayList<Action> getActions() {
        return actions;
    }

    /**
     * Returns the last player action or null if none taken.
     * @return The last action
     */
    public Action getLastAction() {
        if (actions.size() == 0) return null;
        return actions.get(actions.size() - 1);
    }

    /**
     * Returns the player score until this point.
     * @return The current score
     */
    public int getScore() {
        return Environment.getScore(this);
    }

    /**
     * Get the list of perceptions sensed from the current tile.
     * @return The list of perceptions
     */
    protected ArrayList<Perception> getPerceptions() {
        return perceptions;
    }

    /**
     * Sets the list of perceptions sensed from the current tile.
     */
    protected void setPerceptions() {
        perceptions.clear();
        // Senses in the current tile
        if (tile.contains(Item.GOLD)) {
            perceptions.add(Perception.GLITTER);
        }
        // Get the neighbors and find the senses
        int[] neighbors = tile.getNeighbors();
        for (int i = 0; i < neighbors.length; i++) {
            // Sense bumps
            if (neighbors[i] == -1) {
                if (    (i == 0 && direction == Direction.N) ||
                        (i == 1 && direction == Direction.E) ||
                        (i == 2 && direction == Direction.S) ||
                        (i == 3 && direction == Direction.W)) {
                    perceptions.add(Perception.BUMP);
                }
            } else {
                Tile neighbor = world.getPosition(neighbors[i]);
                // Sense a breeze when near a pit
                if (neighbor.contains(Item.PIT)) {
                    perceptions.add(Perception.BREEZE);
                }
                // Sense a stench when near a Wumpus
                if (neighbor.contains(Item.WUMPUS)) {
                    perceptions.add(Perception.STENCH);
                }
            }
        }
    }

    /**
     * Sets the list of perceptions sensed from the current tile.
     * @param value The perception to add to the list
     */
    protected void setPerceptions(Perception value) {
        setPerceptions();
        perceptions.add(value);
    }

    /**
     * Returns weather if the player is alive or not
     * @return <tt>true</tt> if player's alive.
     */
    public boolean isAlive() { return alive; }

    /**
     * Returns weather the player is dead or not.
     * @return <tt>true</tt> if player's dead.
     */
    public boolean isDead() { return !alive; }

    /**
     * Returns weather the player has arrows or not.
     * @return <tt>true</tt> if has arrows.
     */
    public boolean hasArrows() { return arrows > 0; }

    /**
     * Returns if player have picked the gold.
     * @return <tt>true</tt> if has the gold.
     */
    public boolean hasGold() { return gold; }

    /**
     * Returns if agent feels or not a bump.
     * @return If has a bump perception
     */
    public boolean hasBump() {
        return perceptions.contains(Perception.BUMP);
    }

    /**
     * Returns if agent feels or not a breeze.
     * @return <tt>true</tt> if player's feels a breeze
     */
    public boolean hasBreeze() {
        return perceptions.contains(Perception.BREEZE);
    }

    /**
     * Returns if agent feels or not a steche.
     * @return <tt>true</tt> if player's feels a stench
     */
    public boolean hasStench() {
        return perceptions.contains(Perception.STENCH);
    }

    /**
     * Returns if agent hear or not a scream.
     * @return <tt>true</tt> if player's hears a scream
     */
    public boolean hasScream() {
        return perceptions.contains(Perception.SCREAM);
    }

    /**
     * Returns if agent sees or not a glitter.
     * @return <tt>true</tt> if player's sees the glitter.
     */
    public boolean hasGlitter() {
        return perceptions.contains(Perception.GLITTER);
    }

    /**
     * Renders a game board from the player perspective.
     * @return The board representation
     */
    public String render() {
        return world.render();
    }

    /**
     * Returns the player current statuses.
     * @return The debug string
     */
    public String debug() {
        StringBuilder output = new StringBuilder();
        // Position and direction
        output.append("Position: ").append("(").append(x).append(",").append(y).append(",")
                .append(direction).append(")").append("\n");
        // Score
        output.append("Score: ").append(getScore()).append("\n");
        // Perceptions
        output.append("Perceptions: ").append(perceptions.toString());

        return output.toString();
    }
}
