Hunt the Wumpus
===============

Artificial intelligence agent for the Hunt the Wumpus game.


AI Agent
--------

To implement your own AI agent you just need to create a custom class that implements the `wumpus.Agent` interface.

```java
public class CustomAgent implements Agent {
    /**
     * Do something before take the action, probably debug...
     */
    public void beforeAction(Player player) {
        System.out.println(player.render());
        System.out.println(player.debug());
    }

    /**
     * Do something with the player
     */
    public Action getAction(Player player) {
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

    /**
     * Do something after taking the action, probably debug...
     */
    public void afterAction(Player player) {
        // Players Last action
        System.out.println(player.getLastAction());
        // Show a very happy message
        if (player.isDead()) {
            System.out.println("GAME OVER!");
        }
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
// Print the results
System.out.println(world.renderScore());
```

##### Output

```
Results for *CustomAgent*:
+----------------------------+
| Outcome | Score    | Steps |
| ------- | -------- | ----- |
| WIN     | 943      | 50    |
+----------------------------+
```

### Debug

There are some methods to aid debug the agent strategy (don't forget `System.out.println`):

```java
// Renders the player board perspective
player.render();
// Returns current player information
player.debug();
// Returns the last action taken null if none
player.getLastAction();
// Step-by-step debug
Environment.trace();
```

##### Output

```
+---+---+---+---+
|   |   |   |   |
+---+---+---+---+
|   |   |   |   |
+---+---+---+---+
|   |   |   |   |
+---+---+---+---+
|   | → |   |   |
+---+---+---+---+
// player.debug()
Position: (1,3,E)
Score: -1
Perceptions: [BREEZE]
GO_FORWARD
Press ENTER to continue...
```


Deterministic board
-------------------

By default, all the dangers and the gold are setup in random fashion at each run. You can setup the board as you like by calling the setters methods:

```java
// Creates a deterministic world
World world = new World(4, 4);
world.setWumpus(0, 1);
world.setPit(2, 2);
world.setPit(3, 0);
world.setGold(1, 1);
// Show the board
System.out.println(world.renderAll());
```

##### Output

```
+-----+-----+-----+-----+
|     |     |     |   P |
|   ~ |     |   ≈ |     |
+-----+-----+-----+-----+
|   W |   $ |     |     |
|     |   * |   ≈ |   ≈ |
+-----+-----+-----+-----+
|     |     |   P |     |
|   ~ |   ≈ |     |   ≈ |
+-----+-----+-----+-----+
|     |     |     |     |
| →   |     |   ≈ |     |
+-----+-----+-----+-----+
```

### Disclaimer

This game structure and implementation is based on the Wumpus Lite v0.21a of James P. Biagioni of the University of Illinois at Chicago.

Available at [https://www.cs.uic.edu/~jbiagion/wumpuslite.html](https://www.cs.uic.edu/~jbiagion/wumpuslite.html).

### License

Copyright (c) 2016 Edson Hilios. This is a free software is licensed under the MIT License.

*   [Edson Hilios](http://edson.hilios.com.br). Mail me: edson (at) hilios (dot) com (dot) br