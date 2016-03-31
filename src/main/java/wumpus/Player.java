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

    private boolean completed = false;
    private boolean gold = false;
    private int arrows = 3;

    private Block block;
    private ArrayList<Perceptions> perceptions = new ArrayList<Perceptions>();
    private ArrayList<Actions> actions = new ArrayList<Actions>();
    private Direction direction = Direction.E;

    /**
     * Creates a new Player for the given world.
     * @param world The world instance that this player belongs
     */
    public Player(World world) {
        this.world = world;
    }

    /**
     *
     * @return
     */
    public int getX() {
        return block.getX();
    }

    public int getY() {
        return block.getY();
    }


    /**
     * Returns the current block instance.
     * @return The Block instance
     */
    protected Block getBlock() {
        return block;
    }

    /**
     * Set the current block of the agent, un-setting the last one and recalculating all perceptions
     * sensed from the new block.
     * @param x The horizontal position at the board
     * @param y The vertical position at the board
     */
    protected void setBlock(int x, int y) {
        // Remove the Hunter from the
        if (block != null) {
            block.reset(Items.HUNTER);
        }
        block = world.getPosition(x, y);
        block.setItem(Items.HUNTER);
        // Reset senses
        perceptions.clear();
        // Senses in the current block
        if (block.contains(Items.GOLD)) {
            perceptions.add(Perceptions.GLITTER);
        }
        // Get the neighbors and find the senses
        int[] neighbors = block.getNeighbors();
        for (int i = 0; i < neighbors.length; i++) {
            if (neighbors[i] > -1) {
                Block neighbor = world.getPosition(neighbors[i]);
                // Sense a breeze when near a pit
                if (neighbor.contains(Items.PIT)) {
                    perceptions.add(Perceptions.BREEZE);
                }
                // Sense a stench when near a Wumpus
                if (neighbor.contains(Items.WUMPUS)) {
                    perceptions.add(Perceptions.STENCH);
                }
            } else {
                // Sense bumps
                if (i == 0 && direction == Direction.N || i == 1 && direction == Direction.E ||
                        i == 2 && direction == Direction.S || i == 3 && direction == Direction.W) {
                    perceptions.add(Perceptions.BUMP);
                }
            }
        }
    }

    /**
     * Returns weather if the player is alive or not
     * @return The current status
     */
    public boolean isAlive() { return !isDead(); }

    /**
     * Returns weather if the player is dead or not.
     * @return The current status
     */
    public boolean isDead() { return block.contains(Items.WUMPUS) || block.contains(Items.PIT); }

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
     * @return The perception of the action, either a Perceptions.SCREAM or Perceptions.NOTHING
     */
    public Perceptions shootArrow() {
        if (arrows > 0) {
            arrows--;
            int[] neighbors = getBlock().getNeighbors();
            // Select the right neighbor to shoot
            Block neighbor = null;
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
            // Hear a scream
            if (neighbor != null && neighbor.contains(Items.WUMPUS)) {
                // Add the Scream to the current perception
                perceptions.add(Perceptions.SCREAM);
                return Perceptions.SCREAM;
            }
            // Nothing happens
            return Perceptions.NOTHING;
        } else {
            perceptions.add(Perceptions.NO_ARROWS);
            return Perceptions.NO_ARROWS;
        }
    }

    /**
     * Interacts with the world executing an action.
     * @param action The action to take
     */
    protected void setAction(Actions action) {
        actions.add(action);
        // Execute the action
        switch (action) {
            case GO_FORWARD:
                // TODO: Go to other block
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
                if (block.contains(Items.GOLD)) {
                    block.reset(Items.GOLD);
                    gold = true;
                }
                break;
        }
    }

    /**
     * Returns the player actions so far.
     * @return The list of actions
     */
    public ArrayList<Actions> getActions() {
        return actions;
    }

    /**
     * Returns how many actions did the player took.
     * @return The amonut of action
     */
    public int getSteps() {
        return actions.size();
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
    public ArrayList<Perceptions> getPerceptions() {
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
        return perceptions.contains(Perceptions.BUMP);
    }

    /**
     * Returns if agent feels or not a breeze.
     * @return If has a breeze perception
     */
    public boolean hasBreeze() {
        return perceptions.contains(Perceptions.BREEZE);
    }

    /**
     * Returns if agent feels or not a steche.
     * @return If has a stench perception
     */
    public boolean hasStench() {
        return perceptions.contains(Perceptions.STENCH);
    }

    /**
     * Returns if agent hear or not a scream.
     * @return If has a stench perception
     */
    public boolean hasScream() {
        return perceptions.contains(Perceptions.SCREAM);
    }

    /**
     * Returns if agent sees or not a glitter.
     * @return If has a glitter perception
     */
    public boolean hasGlitter() {
        return perceptions.contains(Perceptions.GLITTER);
    }

    /**
     * Renders a game board from the player perspective.
     * @return The board representation
     */
    public String render() {
        return world.render();
    }
}
