package dungeonmania;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class LogicSwitchTests  {
    @Test
    @DisplayName("Test wire can transmit signal")
    public void testBasicLighbulb() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_testBulb", "c_bombTest_placeBombRadius2");
        assertEquals(1, getEntities(res, "light_bulb_off").size());
        assertEquals(0, getEntities(res, "light_bulb_on").size());

        res = dmc.tick(Direction.DOWN);
        

        assertEquals(0, getEntities(res, "light_bulb_off").size());
        assertEquals(1, getEntities(res, "light_bulb_on").size());
    }
    
    @Test
    @DisplayName("Test switch door still acts like regular door")
    public void testSwitchDoorAsKeyDoor() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_testBulb", "c_bombTest_placeBombRadius2");

        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        Position pos = getEntities(res, "player").get(0).getPosition();
        
        // Should stay in same spot because key and wire arent used
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, getInventory(res, "key").size());
        assertEquals(pos, getEntities(res, "player").get(0).getPosition());

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());
        pos = getEntities(res, "player").get(0).getPosition();
        
        // Should be able to walk through door
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, getInventory(res, "key").size());
        assertNotEquals(pos, getEntities(res, "player").get(0).getPosition());
    }  

    @Test
    @DisplayName("Test switch door closes when switch is turned back off")
    public void testSwitchDoorLocksAfterOpen() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_testBulb", "c_bombTest_placeBombRadius2");

        // Activate Switch
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        Position pos = getEntities(res, "player").get(0).getPosition();
        
        // Should not stay in same spot because wire is active
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, getInventory(res, "key").size());
        assertNotEquals(pos, getEntities(res, "player").get(0).getPosition());

        // Push boulder off Switch
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, getInventory(res, "key").size());
        
        // Should not be able to walk through door anymore
        res = dmc.tick(Direction.RIGHT);
        assertEquals(pos, getEntities(res, "player").get(0).getPosition());  
    }  

    @Test
    @DisplayName("Test AND with Adjacent Switches")
    public void testANDlogicAdj() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_testAND", "c_bombTest_placeBombRadius2");
        assertEquals(2, getEntities(res, "light_bulb_off").size());
        assertEquals(0, getEntities(res, "light_bulb_on").size());

        dmc.tick(Direction.DOWN);
        assertEquals(2, getEntities(res, "light_bulb_off").size());
        assertEquals(0, getEntities(res, "light_bulb_on").size());

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        assertEquals(2, getEntities(res, "light_bulb_off").size());
        assertEquals(0, getEntities(res, "light_bulb_on").size());

        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, getEntities(res, "light_bulb_off").size());
        assertEquals(2, getEntities(res, "light_bulb_on").size());
    }

    @Test
    @DisplayName("Test XOR")
    public void testXOR() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_testXOR", "c_bombTest_placeBombRadius2");
        assertEquals(1, getEntities(res, "light_bulb_off").size());
        assertEquals(0, getEntities(res, "light_bulb_on").size());

        // Activate first switch so it should turn on
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, getEntities(res, "light_bulb_off").size());
        assertEquals(1, getEntities(res, "light_bulb_on").size());

        // Should turn off after turning on another switch
        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getEntities(res, "light_bulb_off").size());
        assertEquals(0, getEntities(res, "light_bulb_on").size());

        // Turns back on after removing a switch
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, getEntities(res, "light_bulb_off").size());
        assertEquals(1, getEntities(res, "light_bulb_on").size());
    }

    // @Test
    // @DisplayName("Test CO_AND")
    // public void testCO_AND() {
    //     DungeonManiaController dmc;
    //     dmc = new DungeonManiaController();
    //     DungeonResponse res = dmc.newGame("d_logicTest_testCO_AND", "c_bombTest_placeBombRadius2");
    //     assertEquals(2, getEntities(res, "light_bulb_off").size());
    //     assertEquals(0, getEntities(res, "light_bulb_on").size());

    //     // Activate first switch
    //     res = dmc.tick(Direction.DOWN);
    //     assertEquals(2, getEntities(res, "light_bulb_off").size());
    //     assertEquals(0, getEntities(res, "light_bulb_on").size());

    //     // Should not turn off with 2nd switch as it wasnt on the same tick
    //     res = dmc.tick(Direction.LEFT);
    //     res = dmc.tick(Direction.DOWN);
    //     assertEquals(2, getEntities(res, "light_bulb_off").size());
    //     assertEquals(0, getEntities(res, "light_bulb_on").size());

    //     res = dmc.tick(Direction.LEFT);
    //     assertEquals(1, getEntities(res, "light_bulb_off").size());
    //     assertEquals(1, getEntities(res, "light_bulb_on").size());
    // }
}


