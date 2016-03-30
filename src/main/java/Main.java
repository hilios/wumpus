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
        } catch (Exception e) {
            throw e;
        }
    }
}
