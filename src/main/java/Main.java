import wumpus.Agent;
import wumpus.World;

/**
 * Entry point for the application.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        try {
            World wumpus = new World(4, 4);

            // Print the game title :)
            System.out.println(wumpus.renderTitle());

            // Start and execute the AI agent
            Agent ai = new RandomAgent();
            wumpus.execute(ai);

            // Print the score table
            System.out.println("Result");
            System.out.println(wumpus.renderScore());
            System.out.println(wumpus.renderAll());

        } catch (Exception e) {
            throw e;
        }
    }
}
