package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Position;

public class Ext2Tests {
    @Test
    @DisplayName("Creates a valid dungeon response")
    public void testValidDungeonResponse() {
        DungeonManiaController dmc = new DungeonManiaController();
        assertTrue(dmc.generateDungeon(1, 1, 3, 3, "c_movementTest_testMovementDown") != null);
    }
    @Test
    @DisplayName("Throws exception when invalid dungeon response")
    public void testInvalidConfig() {
        DungeonManiaController dmc = new DungeonManiaController();
        assertThrows(IllegalArgumentException.class, ()->dmc.generateDungeon(1,1,3,3,"invalid_123"));
    }

    @Test
    @DisplayName("Check if wall encasing dungeon is created and checks if exit and starting position is clear")
    public void testValidDungeonGeneration() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.generateDungeon(1,1,3,3,"c_movementTest_testMovementDown");
        // Wall rectangle is from (0,0) to (4,4)
        DungeonResponse response = dmc.getDungeonResponseModel();
        response.getEntities();
        HashMap<Position, Boolean> wallEnclosure = new HashMap<>();
        
        // Populate hashMap
        for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 4; j++) {
                if (i == 0 || i == 4 || j == 0 || j == 4) {
                    Position coords = new Position(i, j);
                        wallEnclosure.put(coords, false);
                }
            }
        }

        response.getEntities().stream().forEach(x -> {
            if (x.getType().equals("wall") && wallEnclosure.containsKey(x.getPosition())) {
                wallEnclosure.put(x.getPosition(),true);
            } else if (x.getType().equals("exit")) {
                assert(x.getPosition().equals(new Position(3, 3)));
            }
        });

        // Every value should be true
        for (Boolean bool : wallEnclosure.values()) {
            assertTrue(bool);
        }
    }



}
