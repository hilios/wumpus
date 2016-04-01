import agents.RandomAgent;
import wumpus.Agent;
import wumpus.World;

/**
 * Entry point for the application.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        try {
            World world = new World(4, 4);

            // Print the game title :)
            System.out.println(world.renderTitle());

            // Start and execute the AI agent
            Agent agent = new RandomAgent();
            world.execute(agent);

            // Print the board and score table
            System.out.println("Board:");
            System.out.println(world.renderAll());

            System.out.println("Results for *" + world.getAgentName() + "*:");
            System.out.println(world.renderScore());

        } catch (Exception e) {
            throw e;
        }
    }
}
