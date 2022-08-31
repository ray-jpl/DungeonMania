package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


import static dungeonmania.TestUtils.getInventory;
import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getPlayer;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import dungeonmania.response.models.DungeonResponse;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import dungeonmania.response.models.EntityResponse;


public class CollectablesTests {
    @Test
    @DisplayName("Test Player can pick up items")
    public void testPickUpKey() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_useWrongKeyOnDoor", "c_DoorsKeysTest");

        // pick up key
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());
    }

    @Test
    @DisplayName("Test player cannot use wrong key to open the door")
    public void testUseWrongKeyOnDoor() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_useWrongKeyOnDoor", "c_DoorsKeysTest");

        // pick up key
        res = dmc.tick(Direction.RIGHT);
        Position pos = getEntities(res, "player").get(0).getPosition();
        assertEquals(1, getInventory(res, "key").size());

        // Try to walk through door and check key is still here
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());
        assertEquals(pos, getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("Test player cannot pick up two keys")
    public void testPickingUpTwoKeys() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_pickingUpTwoKeys", "c_DoorsKeysTest");

        // pick up key1
        res = dmc.tick(Direction.RIGHT);
        Position pos = getEntities(res, "player").get(0).getPosition();
        assertEquals(1, getInventory(res, "key").size());

        // Walk over key2
        res = dmc.tick(Direction.RIGHT);
        // Still have only one key
        assertEquals(1, getInventory(res, "key").size());
        assertNotEquals(pos, getEntities(res, "player").get(0).getPosition());
        pos = getEntities(res, "player").get(0).getPosition();

        // Fail to walk through Door2 as player should be holding Key1
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());
        assertEquals(pos, getEntities(res, "player").get(0).getPosition());
    }


    @Test
    @DisplayName("Test player cant walk into door without holding any keys")
    public void testWalkIntoDoorWithNoKey() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_UnlockByKey", "c_DoorsKeysTest");

        Position pos = getEntities(res, "player").get(0).getPosition();
        // Try move Right
        res = dmc.tick(Direction.RIGHT);

        assertEquals(pos, getEntities(res, "player").get(0).getPosition());
    }

 
    @Test
    @DisplayName("Test Player cannot walk into placed bomb")
    public void testBombPlacementMovement() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_bombPlacementMovement", "c_bombTest");
        
        // Pick up bomb
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "bomb").size());
        String bombId = getInventory(res, "bomb").get(0).getId();
        
        // Place Bomb
        res = assertDoesNotThrow(() -> dmc.tick(bombId), "message");
       
        res = dmc.tick(Direction.LEFT);
        Position pos = getEntities(res, "player").get(0).getPosition();
        // Should not be able to walk back into bomb
        res = dmc.tick(Direction.RIGHT);
        assertEquals(pos, getEntities(res, "player").get(0).getPosition());

    }

    @Test
    @DisplayName("Test Bomb explodes instantly")
    public void testBombActivatedFirst() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius2", "c_bombTest_placeBombRadius2");

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);

        // Pick up Bomb
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "bomb").size());

        // Items should still exist as switch is not active
        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "boulder").size());
        assertEquals(1, getEntities(res, "switch").size());
        assertEquals(2, getEntities(res, "wall").size());
        assertEquals(2, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());

        // Place Cardinally Adjacent
        res = dmc.tick(Direction.RIGHT);
        String bombId = getInventory(res, "bomb").get(0).getId();
        res = assertDoesNotThrow(() -> dmc.tick(bombId), "message");

        // Check Bomb exploded with radius 2
        //
        //              Boulder/Switch      Wall            Wall
        //              Bomb                Treasure
        //
        //              Treasure
        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(0, getEntities(res, "boulder").size());
        assertEquals(0, getEntities(res, "switch").size());
        assertEquals(0, getEntities(res, "wall").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());

        // Test player is functioning
        res = dmc.tick(Direction.RIGHT);
        EntityResponse initPlayer = getPlayer(res).get();
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(),
                                initPlayer.getType(), new Position(5, 3), false);
        EntityResponse actualPlayer = getPlayer(res).get();
        assertEquals(expectedPlayer, actualPlayer);
    }

    @Test
    @DisplayName("Test Bomb only explodes when switch is activated")
    public void testBombPlacedThenActivatedAfter() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius2", "c_bombTest_placeBombRadius2");

        // Pick up Bomb
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "bomb").size());

        // Place Cardinally Adjacent
        res = dmc.tick(Direction.RIGHT);
        String bombId = getInventory(res, "bomb").get(0).getId();
        res = assertDoesNotThrow(() -> dmc.tick(bombId), "message");

        // Items should still exist as switch is not active
        assertEquals(1, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "boulder").size());
        assertEquals(1, getEntities(res, "switch").size());
        assertEquals(2, getEntities(res, "wall").size());
        assertEquals(2, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());


        // Activate Switch
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);

        // Check Bomb exploded with radius 2
        //
        //              Boulder/Switch      Wall            Wall
        //              Bomb                Treasure
        //
        //              Treasure
        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(0, getEntities(res, "boulder").size());
        assertEquals(0, getEntities(res, "switch").size());
        assertEquals(0, getEntities(res, "wall").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());

        // Test player is functioning
        res = dmc.tick(Direction.RIGHT);
        EntityResponse initPlayer = getPlayer(res).get();
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(),
                                initPlayer.getType(), new Position(4, 2), false);
        EntityResponse actualPlayer = getPlayer(res).get();
        assertEquals(expectedPlayer, actualPlayer);
    }

    @Test
    @DisplayName("Test Bombs that havent been placed dont explode")
    public void testNewBombOnActiveSwitch() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius2BombNextToSwitch", "c_bombTest_placeBombRadius2");

        // Check items are there
        assertEquals(1, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "boulder").size());
        assertEquals(1, getEntities(res, "switch").size());
        assertEquals(2, getEntities(res, "wall").size());
        assertEquals(2, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());


        // Activate Switch
        res = dmc.tick(Direction.RIGHT);

        // Items should still exist as bomb was not placed down there
        assertEquals(1, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "boulder").size());
        assertEquals(1, getEntities(res, "switch").size());
        assertEquals(2, getEntities(res, "wall").size());
        assertEquals(2, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());
    }
}
