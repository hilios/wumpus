package wumpus;

import java.util.ArrayList;

import wumpus.Environment.*;

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

    private Block block;
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
        return block.getX();
    }

    /**
     * Get the vertical position of the player at the board.
     * @return The Y position
     */
    public int getY() {
        return block.getY();
    }

    /**
     * Resets the player state.
     */
    protected void reset() {
        arrows = 3;
        gold = false;
        completed = false;
        direction = Direction.E;
        perceptions.clear();
        actions.clear();
    }

    /**
     * Returns the current block instance.
     * @return The Block instance
     */
    protected Block getBlock() {
        return block;
    }

    /**
     * Set the current block of the agent by its 2D position
     * @param x The horizontal position
     * @param y The vertical position
     */
    protected void setBlock(int x, int y) {
        int index = world.getIndex(x, y);
        setBlock(index);
    }
    /**
     * Set the current block of the agent, un-setting the last one and recalculating all perceptions
     * sensed from the new block.

     */
    protected void setBlock(int index) {
        // Remove the Hunter from the
        if (block != null) {
            block.reset(Item.HUNTER);
        }
        block = world.getPosition(index);
        block.setItem(Item.HUNTER);
        //
        x = block.getX();
        y = block.getY();
        // Check if player is still alive
        alive = !(block.contains(Item.WUMPUS) || block.contains(Item.PIT));
        if (isDead()) return;
        // Reset senses
        perceptions.clear();
        // Senses in the current block
        if (block.contains(Item.GOLD)) {
            perceptions.add(Perception.GLITTER);
        }
        // Get the neighborhood and find the senses
        int[] neighborhood = block.getNeighborhood();
        for (int i = 0; i < neighborhood.length; i++) {
            if (neighborhood[i] > -1) {
                Block neighbor = world.getPosition(neighborhood[i]);
                // Sense a breeze when near a pit
                if (neighbor.contains(Item.PIT)) {
                    perceptions.add(Perception.BREEZE);
                }
                // Sense a stench when near a Wumpus
                if (neighbor.contains(Item.WUMPUS)) {
                    perceptions.add(Perception.STENCH);
                }
            } else {
                // Sense bumps
                if (i == 0 && direction == Direction.N || i == 1 && direction == Direction.E ||
                        i == 2 && direction == Direction.S || i == 3 && direction == Direction.W) {
                    perceptions.add(Perception.BUMP);
                }
            }
        }
    }

    /**
     * Returns weather if the player is alive or not
     * @return The current status
     */
    public boolean isAlive() { return alive; }

    /**
     * Returns weather if the player is dead or not.
     * @return The current status
     */
    public boolean isDead() { return !alive; }

    /**
     * Returns if the player have win or loose the game.
     * @return The outcome of the game
     */
    public Result getResult() {
        if (completed) return Result.WIN;
        return Result.LOOSE;
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
            int[] neighborhood = getBlock().getNeighborhood();
            // Select the right neighbor to shoot
            Block neighbor = null;
            switch (direction) {
                case N:
                    if (neighborhood[0] > -1) neighbor = world.getPosition(neighborhood[0]);
                    break;
                case E:
                    if (neighborhood[1] > -1) neighbor = world.getPosition(neighborhood[1]);
                    break;
                case S:
                    if (neighborhood[2] > -1) neighbor = world.getPosition(neighborhood[2]);
                    break;
                case W:
                    if (neighborhood[3] > -1) neighbor = world.getPosition(neighborhood[3]);
                    break;
            }
            // Hear a scream
            if (neighbor != null && neighbor.contains(Item.WUMPUS)) {
                // Add the Scream to the current perception
                perceptions.add(Perception.SCREAM);
                return Perception.SCREAM;
            }
            // Nothing happens
            return Perception.NOTHING;
        } else {
            perceptions.add(Perception.NO_ARROWS);
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
                int[] neighborhood = block.getNeighborhood();
                switch (direction) {
                    case N:
                        if (neighborhood[0] > -1) setBlock(neighborhood[0]);
                        break;
                    case E:
                        if (neighborhood[1] > -1) setBlock(neighborhood[1]);
                        break;
                    case S:
                        if (neighborhood[2] > -1) setBlock(neighborhood[2]);
                        break;
                    case W:
                        if (neighborhood[3] > -1) setBlock(neighborhood[3]);
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
            case SHOOT:
                shootArrow();
                break;
            case GRAB:
                // If block has gold pick and remove from the block
                if (block.contains(Item.GOLD)) {
                    block.reset(Item.GOLD);
                    gold = true;
                }
                break;
            case GIVE_UP:
                alive = false;
                break;
        }
    }

    /**
     * Returns the player actions so far.
     * @return The list of actions
     */
    public ArrayList<Action> getActions() {
        return actions;
    }

    /**
     * Returns the player score until this point.
     * @return The current score
     */
    public int getScore() {
        return Environment.getScore(this);
    }

    /**
     * Returns the list of perceptions sensed from the current block.
     * @return The list of perceptions
     */
    public ArrayList<Perception> getPerceptions() {
        return perceptions;
    }

    /**
     * Returns if player have picked the gold.
     * @return If picked the gold
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
     * @return If has a breeze perception
     */
    public boolean hasBreeze() {
        return perceptions.contains(Perception.BREEZE);
    }

    /**
     * Returns if agent feels or not a steche.
     * @return If has a stench perception
     */
    public boolean hasStench() {
        return perceptions.contains(Perception.STENCH);
    }

    /**
     * Returns if agent hear or not a scream.
     * @return If has a stench perception
     */
    public boolean hasScream() {
        return perceptions.contains(Perception.SCREAM);
    }

    /**
     * Returns if agent sees or not a glitter.
     * @return If has a glitter perception
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
}
