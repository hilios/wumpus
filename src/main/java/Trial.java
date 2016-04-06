import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import agents.HeuristicAgent;
import agents.RandomAgent;
import wumpus.Agent;
import wumpus.World;

/**
 * Executes trials run for the agents and safe the output to and CSV file.
 */
public class Trial {
    private static final String DEFAULT_REPORT_FOLDER = "./target/trial-reports";
    private static final int TRIALS = 1000;

    public static void main(String[] args) throws Exception {
        // Create a 4x4 world
        final World world = new World(4, 4);

        // Trial for HeuristicAgent
        new Trial("HeuristicAgent", world, new TrialAgent() {
            public Agent getAgent() {
                HeuristicAgent agent = new HeuristicAgent(world.getWidth(), world.getWidth());
                agent.setDebug(false);
                return agent;
            }
        });

        // Trial for RandomAgent
        new Trial("RandomAgent", world, new TrialAgent() {
            public Agent getAgent() {
                RandomAgent agent = new RandomAgent();
                agent.setDebug(false);
                return agent;
            }
        });

        System.out.println("FINISHED!");
    }

    /**
     * Defines the Agent that will be run at the trial.
     */
    public interface TrialAgent {
        Agent getAgent();
    }

    /**
     * Executes the trial run to some agent and saves a CSV report wit the results.
     * @param name The trial name
     * @param world The world instance
     * @param trialAgent The agent implementation
     */
    public Trial(String name, World world, TrialAgent trialAgent) {
        // Report file path
        String reportFilepath = String.format("%s/%s.csv", DEFAULT_REPORT_FOLDER, name);

        // Create reports folder
        File reportsFolder = new File(reportFilepath);
        if (reportsFolder.exists()) reportsFolder.mkdir();
        // Run trial
        long executionTime;
        try {
            // Create report writer
            FileWriter writer = new FileWriter(reportFilepath);
            // Header
            writer.append("Result,Score,Steps,Execution time (MS)\n");

            for (int i = 0; i < TRIALS; i++) {
                // Execute the agent
                executionTime = System.currentTimeMillis();
                world.execute(trialAgent.getAgent());
                executionTime = System.currentTimeMillis() - executionTime;
                // Get agent score
                String result = String.format("%s,%d,%d,%d%n", world.getResult(),
                        world.getPlayer().getScore(), world.getPlayer().getActions().size(),
                        executionTime);
                writer.append(result);
            }
            // Output file
            writer.flush();
            writer.close();
        } catch (IOException error) {
            error.printStackTrace();
        } catch (InterruptedException error) {
            error.printStackTrace();
        }
        // Output
        System.out.format("Trial report at: %s%n", reportFilepath);
    }
}
