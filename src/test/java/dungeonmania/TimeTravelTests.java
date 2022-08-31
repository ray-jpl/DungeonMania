package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TimeTravelTests {
    @Test
    @DisplayName("Invalid rewind arguments")
    public void testInvalidRewind() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("bribeAssFail",
                "bribe_amount_3Ass");
        dmc.tick(Direction.RIGHT);
        assertThrows(IllegalArgumentException.class, () -> {
            dmc.rewind(-1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            dmc.rewind(10);
        });
    }

    @Test
    @DisplayName("Invalid rewind works for less than 30 ticks")
    public void testPortalRewind1() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("portalRewind1",
                "bribe_amount_3Ass");
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(0, 0), getEntities(res, "older_player").get(0).getPosition());
        assertEquals(new Position(2, 0), getEntities(res, "player").get(0).getPosition());
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(res.getEntities().size(), 3);
    }

    @Test
    @DisplayName("Invalid rewind works for more than 30 ticks")
    public void testPortalRewind2() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("portalRewind2",
                "bribe_amount_3Ass");
        for (int i = 0; i < 35; i++) {
            res = dmc.tick(Direction.RIGHT);
        }
        assertEquals(new Position(5, 0), getEntities(res, "older_player").get(0).getPosition());
        assertEquals(new Position(35, 0), getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("Invalid rewind retains inventory older player interacts and collects [Integration Test]")
    public void testRewindInventory() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("rewindTest",
                "M3_configNoFail");
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        String zombId = getEntities(res, "zombie_toast_spawner").get(0).getId();
        assertDoesNotThrow(() -> {
            dmc.interact(zombId);
        });
        dmc.tick(Direction.RIGHT);
        res = dmc.rewind(4);
        assertEquals(new Position(0, 0), getEntities(res, "older_player").get(0).getPosition());
        assertEquals(new Position(3, -1), getEntities(res, "zombie_toast_spawner").get(0).getPosition());
        assertEquals(1, getInventory(res, "sword").size());
        assertEquals(0, getInventory(res, "time_turner").size());
        assertEquals(new Position(2, 0), getEntities(res, "time_turner").get(0).getPosition());
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, getEntities(res, "time_turner").size());
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, getEntities(res, "zombie_toast_spawner").size());
    }

}
