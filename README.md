# Wumpus

Artificial intelligence agent for the Wumpus game.


## AI Agent

To implement your own AI agent you just need to create a custom class that implements the `wumpus.Agent` interface.

```java
public class EstimationAgent implements Agent {
    /**
     * Do something with the player
     */
    public Action getAction(Player player) {
        int x = player.getX();
        int y = player.getY();
        // Feel the perceptions
        boolean bump = player.hasBump();
        boolean breeze = player.hasBreeze();
        boolean stench = player.hasStench();
        boolean scream = player.hasScream();
        boolean glitter = player.hasGlitter();

        if (bump || breeze || stench || scream || glitter) {
            // TODO: Do some action based on the players perception...
        }

        return Action.NO_OP;
    }
}
```

The return of the `getAction` method is the next player move. From this method you can access all `Player` perceptions and informations.

To run your custom Agent call it from the `Main` class at some `World` instance.

```java
// Instantiate the AI agent
Agent agent = new CustomAgent();
// Create an world and execute the agent
World world = new World(4, 4);
world.execute(agent);
```

## Output

```sh
 _       __                                    _       __           __    __
| |     / /_  ______ ___  ____  __  _______   | |     / /___  _____/ /___/ /
| | /| / / / / / __ `__ \/ __ \/ / / / ___/   | | /| / / __ \/ ___/ / __  /
| |/ |/ / /_/ / / / / / / /_/ / /_/ (__  )    | |/ |/ / /_/ / /  / / /_/ /
|__/|__/\__,_/_/ /_/ /_/ .___/\__,_/____/     |__/|__/\____/_/  /_/\__,_/
                      /_/
+---+---+---+---+
|   |   |   |   |
+---+---+---+---+
|   |   |   |   |
+---+---+---+---+
|   |   |   |   |
+---+---+---+---+
| → |   |   |   |
+---+---+---+---+
Block: (0,3)
Perceptions:
Action: GO_FORWARD

[...]

+---+---+---+---+
|   |   |   |   |
+---+---+---+---+
|   |   |   |   |
+---+---+---+---+
|   |   |   |   |
+---+---+---+---+
|   | ↑ |   |   |
+---+---+---+---+
Block: (1,3)
Perceptions: > STENCH > BREEZE
Action: GO_FORWARD

Board:
+-----+-----+-----+-----+
|     |     |     |     |
|   ≈ |     |     |     |
+-----+-----+-----+-----+
|   P |     |     |     |
|     |   ~ |     |     |
+-----+-----+-----+-----+
|     |   W |     |   $ |
|   ≈ | †   |   ≈ |   * |
+-----+-----+-----+-----+
|     |     |   P |     |
|     |   ~ |     |   ≈ |
+-----+-----+-----+-----+
Results for *ai.RandomAgent*:
+----------------------------+
| Outcome | Score    | Steps |
| ------- | -------- | ----- |
| LOOSE   | -1005    | 5     |
+----------------------------+
```

