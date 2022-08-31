package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static dungeonmania.TestUtils.getGoals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

public class GoalsTests {

    @Test
    @DisplayName("Testing a map with basic goal of moving boulders onto switch")
    public void basicGoalBoulders() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_goalTests_basicBouldersGoal",
                "c_basicLayout");

        assertTrue(getGoals(res).contains(":boulders"));
        res = dmc.tick(Direction.RIGHT);
        assertEquals("", getGoals(res));

    }

    @Test
    @DisplayName("Testing a map with basic goal of killing enemies")
    public void basicGoalEnemies() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_goalTests_basicEnemiesGoal",
                "c_spiderTest_basicMovement");

        assertTrue(getGoals(res).contains(":enemies"));
        // kill spider on first tick - as spider moves up on first tick
        res = dmc.tick(Direction.RIGHT);
        assertEquals("", getGoals(res));

    }

    @Test
    @DisplayName("Testing a map with basic goal of exiting the map")
    public void basicGoalExit() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_movementTest_testMovementDown",
                "c_basicLayout");
        assertTrue(getGoals(res).contains(":exit"));
        // move down to the exit
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals("", getGoals(res));

    }

    @Test
    @DisplayName("Testing a map with basic goal of picking treasure")
    public void basicGoalTreasure() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_goalTests_basicTreasureGoal",
                "c_basicLayout");
        assertTrue(getGoals(res).contains(":treasure"));

        // Pick up treasure to the right of the player
        res = dmc.tick(Direction.RIGHT);
        assertEquals("", getGoals(res));

    }

    @Test
    @DisplayName("Testing treasure goal of 2")
    public void multipleTreasureGoal() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_twoTreasure",
                "c_multipleTreasure");
        assertTrue(getGoals(res).contains(":treasure"));

        // Pick up treasure to the right of the player
        res = dmc.tick(Direction.RIGHT);
        assertTrue(getGoals(res).contains(":treasure"));
        // Picks up another treasure to complete the goal
        res = dmc.tick(Direction.RIGHT);
        // Goal should be complete
        assertEquals("", getGoals(res));

    }

    // Complex goals

    @Test
    @DisplayName("Testing a map with complex goal - boulder AND exit (exit before boulder should not work)")
    public void complexGoalAND() {

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalsExit",
                "c_basicLayout");
        assertTrue(getGoals(res).contains(":boulders"));
        // exit must be completed at the end
        assertTrue(getGoals(res).contains("exit"));

        // player (1, 1) -> -> boulder (2, 1) -> switch (3, 1)
        // exit (3, 2)

        // try exiting first - should the game end (lost) or should it not allow to exit
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT); // position remains the same
        // still contains the exit goal as player cannot exit unless the boulder goal is
        // completed
        assertTrue(getGoals(res).contains("exit"));

        // move back to push boulder onto switch
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);

        // does not contain this goal since it is completed
        assertFalse(getGoals(res).contains("boulders"));

        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        assertFalse(getGoals(res).contains("exit"));
        // all goals are completed
        assertEquals("", getGoals(res));

    }

    @Test
    @DisplayName("Testing a map with complex goal - enemy OR treasure")
    public void complexGoalOR() {

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalsEnemy",
                "c_spiderTest_basicMovement");

        // Do not have to complete both
        assertTrue(getGoals(res).contains(":enemies"));
        assertTrue(getGoals(res).contains("treasure"));

        // kill spider
        res = dmc.tick(Direction.RIGHT);
        assertFalse(getGoals(res).contains(":enemies"));
        // Goals are finished since one of the goal is completed and game is completed
        // Removes the entire goals string
        assertEquals("", getGoals(res));

    }

    @Test
    @DisplayName("Testing a map with complex goal - enemy OR treasure")
    public void complexGoalOR2() {

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalsEnemy",
                "c_spiderTest_basicMovement");

        // Do not have to complete both
        assertTrue(getGoals(res).contains("treasure"));
        assertTrue(getGoals(res).contains(":enemies"));

        // picks up treasure and completes
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        // Goals are finished since one of the goal is completed and game is completed
        // Removes the entire goals string
        assertEquals("", getGoals(res));

    }

    @Test
    @DisplayName("Testing a map with nested complex goal - boulder AND (exit or (Treasure and enemies))")
    public void nestedComplexGoal() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_nestedGoals", "c_basicLayout");

        // kill spider, move boulder, collect treasure
        assertTrue(getGoals(res).contains(":enemies"));
        assertTrue(getGoals(res).contains("boulders"));
        assertTrue(getGoals(res).contains("treasure"));
        assertTrue(getGoals(res).contains(":exit"));

        // kill enemy
        res = dmc.tick(Direction.RIGHT);
        assertFalse(getGoals(res).contains(":enemies"));
        assertTrue(getGoals(res).contains("boulders"));
        assertTrue(getGoals(res).contains("treasure"));
        assertTrue(getGoals(res).contains(":exit"));

        // move boulder ontop of switch
        res = dmc.tick(Direction.RIGHT);
        assertFalse(getGoals(res).contains(":enemies"));
        assertFalse(getGoals(res).contains("boulders"));
        assertTrue(getGoals(res).contains("treasure"));
        assertTrue(getGoals(res).contains(":exit"));

        // collect treasure
        res = dmc.tick(Direction.DOWN);
        // No goals should be left
        assertEquals("", getGoals(res));

    }

    @Test
    @DisplayName("Testing a goal being unachieved after being achieved - boulder moved off switch")
    public void unachievedGoal() {

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalsExit", "c_basicLayout");

        assertTrue(getGoals(res).contains("boulders"));
        // move boulder ontop of switch
        res = dmc.tick(Direction.RIGHT);
        assertFalse(getGoals(res).contains("boulders"));

        // move boulder off the switch - goal becomes unachieved
        res = dmc.tick(Direction.RIGHT);
        assertTrue(getGoals(res).contains("boulders"));

    }

}
