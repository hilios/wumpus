import wumpus.Environment;
import wumpus.Player;
import wumpus.World;

/**
 * Entry point for the application.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        try {
            World world = new World(4, 4);

            System.out.println("Wumpus World!");
            System.out.println(world.render());

            for (Player player : world.start()) {
                player.setAction(Environment.Actions.TURN_LEFT);
                System.out.println(world.render());
            }

            Player player = world.getPlayer();
            System.out.println("\nResults");
            System.out.println("| Outcome | Score    | Steps |");
            System.out.println("| ------- | -------- | ----- |");
            System.out.print("| " + player.getResult());
            System.out.print(" | " + player.getScore());
            System.out.print(" | " + player.getSteps());
            System.out.print(" |\n");

        } catch (Exception e) {
            throw e;
        }
    }
}
