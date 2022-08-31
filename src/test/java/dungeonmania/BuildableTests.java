package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static dungeonmania.TestUtils.getInventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;


import dungeonmania.util.Direction;


public class BuildableTests {
    @Test
    @DisplayName("Test an exception is thrown when making something that isnt a bow or shield")
    public void testInvalidBuildable() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_movementTest_testMovementDown",
                "c_movementTest_testMovementDown");

        assertThrows(IllegalArgumentException.class, () -> {
            dmc.build("notBowOrShield");
        });
    }

    @Test
    @DisplayName("Test crafting buildable without necessary materials")
    public void testBuildableInsufficientMaterials() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_movementTest_testMovementDown",
                "c_movementTest_testMovementDown");

        assertThrows(InvalidActionException.class, () -> {
            dmc.build("bow");
        });

        assertThrows(InvalidActionException.class, () -> {
            dmc.build("shield");
        });
    }

    @Test
    @DisplayName("Test crafting bow")
    public void testBuildableBow() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildTest_buildBow",
                "c_movementTest_testMovementDown");
        
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(3, getInventory(res, "arrow").size());
        assertEquals(1, getInventory(res, "wood").size());
        
        assertDoesNotThrow(()-> dmc.build("bow"));
        try {
            res = dmc.build("bow");
            // Check that materials are used and we have bow
            assertEquals(1, getInventory(res, "bow").size());
            assertEquals(0, getInventory(res, "arrow").size());
            assertEquals(0, getInventory(res, "wood").size());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidActionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @Test
    @DisplayName("Test crafting Shield with Key")
    public void testBuildableShieldKey() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildTest_buildShield",
                "c_movementTest_testMovementDown");
        
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(2, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "key").size());
        
        assertDoesNotThrow(()-> dmc.build("shield"));

        try {
            res = dmc.build("shield");
            // Check that materials are used and we have shield
            assertEquals(0, getInventory(res, "wood").size());
            assertEquals(0, getInventory(res, "key").size());
            assertEquals(1, getInventory(res, "shield").size());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidActionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    @DisplayName("Test crafting Shield with treasure")
    public void testBuildableShieldTreasure() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildTest_buildShieldTreasure",
                "c_movementTest_testMovementDown");
        
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(2, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "treasure").size());
        
        assertDoesNotThrow(()-> dmc.build("shield"));
        try {
            res = dmc.build("shield");
            // Check that materials are used and we have shield
            assertEquals(0, getInventory(res, "wood").size());
            assertEquals(0, getInventory(res, "treasure").size());
            assertEquals(1, getInventory(res, "shield").size());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidActionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // ASSUMPTION THAT TREASURE ALWAYS GETS CONSUMED BEFORE KEY
    @Test
    @DisplayName("Test crafting Shield with both treasure and key in inventory")
    public void testBuildableShieldTreasureKey() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildTest_buildShieldTreasure",
                "c_movementTest_testMovementDown");
        
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(2, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "key").size());
        
        assertDoesNotThrow(()-> dmc.build("shield"));
        try {
            res = dmc.build("shield");
            // Check that materials are used and we have bow
            assertEquals(0, getInventory(res, "wood").size());
            assertEquals(0, getInventory(res, "treasure").size());
            assertEquals(1, getInventory(res, "key").size());
            assertEquals(1, getInventory(res, "shield").size());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidActionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        

    }
}
