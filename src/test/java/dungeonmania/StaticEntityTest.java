package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static dungeonmania.TestUtils.getPlayer;
import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getGoals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class StaticEntityTest {
        @Test
        @DisplayName("Test the player can't move through walls")
        public void testWall() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("d_staticTest_testWall",
                                "c_movementTest_testMovementDown");
                EntityResponse initPlayer = getPlayer(res).get();

                // Player shouldn't move
                EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(),
                                initPlayer.getType(), new Position(1, 1), false);

                // move player downward
                res = dmc.tick(Direction.DOWN);
                EntityResponse actualPlayer = getPlayer(res).get();

                // assert after movement
                assertEquals(expectedPlayer, actualPlayer);

                // test other directions
                res = dmc.tick(Direction.UP);
                actualPlayer = getPlayer(res).get();
                assertEquals(expectedPlayer, actualPlayer);

                res = dmc.tick(Direction.LEFT);
                actualPlayer = getPlayer(res).get();
                assertEquals(expectedPlayer, actualPlayer);

                res = dmc.tick(Direction.RIGHT);
                actualPlayer = getPlayer(res).get();
                assertEquals(expectedPlayer, actualPlayer);
        }

        @Test
        @DisplayName("Test the player can move through portals")
        public void testPortal() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("d_staticTest_testPortal",
                                "c_movementTest_testMovementDown");
                EntityResponse initPlayer = getPlayer(res).get();

                // Player should move to only free spot
                // Tests portal pairs are correct
                EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(),
                                initPlayer.getType(), new Position(1, 21), false);

                // move player downward
                res = dmc.tick(Direction.DOWN);
                EntityResponse actualPlayer = getPlayer(res).get();

                // assert after movement
                assertEquals(expectedPlayer, actualPlayer);

                // assert player can move
                res = dmc.tick(Direction.DOWN);
                expectedPlayer = new EntityResponse(initPlayer.getId(),
                                initPlayer.getType(), new Position(1, 22), false);
                actualPlayer = getPlayer(res).get();
                assertEquals(expectedPlayer, actualPlayer);
        }

        @Test
        @DisplayName("Test the player can't move through wall blocked portals")
        public void testPortalBlock() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("d_staticTest_testPortalBlock",
                                "c_movementTest_testMovementDown");
                EntityResponse initPlayer = getPlayer(res).get();

                // Player shouldn't move
                EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(),
                                initPlayer.getType(), new Position(1, 1), false);

                // move player downward
                res = dmc.tick(Direction.DOWN);
                EntityResponse actualPlayer = getPlayer(res).get();

                // assert after movement
                assertEquals(expectedPlayer, actualPlayer);
        }

        @Test
        @DisplayName("Test the player can move through onto other portals")
        public void testPortalDouble() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("d_staticTest_testPortalNotBlock",
                                "c_movementTest_testMovementDown");
                EntityResponse initPlayer = getPlayer(res).get();

                // Player should move
                EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(),
                                initPlayer.getType(), new Position(20, 0), false);

                // move player downward
                res = dmc.tick(Direction.DOWN);
                EntityResponse actualPlayer = getPlayer(res).get();

                // assert after movement
                assertEquals(expectedPlayer, actualPlayer);
        }

        @Test
        @DisplayName("Test spider can move over portals unaffected")
        public void testPortalSpider() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("d_staticTest_testPortalSpider",
                                "c_movementTest_testMovementDown");

                res = dmc.tick(Direction.DOWN);
                Position actualPos = getEntities(res, "spider").get(0).getPosition();
                Position expectedPos = new Position(1, 1);
                assertEquals(expectedPos, actualPos);

                res = dmc.tick(Direction.DOWN);
                actualPos = getEntities(res, "spider").get(0).getPosition();
                expectedPos = new Position(2, 1);
                assertEquals(expectedPos, actualPos);
        }

        // Merc treats portals as walls, cannot currently test
        // @Test
        // @DisplayName("Test merc can move through portals")
        // public void testPortalMercenary() {
        // DungeonManiaController dmc = new DungeonManiaController();
        // DungeonResponse res = dmc.newGame("d_staticTest_testPortalMercenary",
        // "c_movementTest_testMovementDown");
        //
        // res = dmc.tick(Direction.RIGHT);
        //
        // Position Pos = getEntities(res, "mercenary").get(0).getPosition();
        // Position exPos1 = new Position(1, 1);
        // Position exPos2 = new Position(1, 3);
        // Position exPos3 = new Position(0, 2);
        // Position exPos4 = new Position(2, 2);
        //
        // System.out.print(Pos);
        //
        // Boolean onPortal = Pos.equals(exPos1) || Pos.equals(exPos2) ||
        // Pos.equals(exPos3) || Pos.equals(exPos4);
        // assertTrue(onPortal);
        // }
        @Test
        @DisplayName("Test zombie unaffected by portals")
        public void testPortalZombie() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("d_staticTest_testPortalZombie",
                                "c_movementTest_testMovementDown");

                res = dmc.tick(Direction.RIGHT);

                Position Pos = getEntities(res, "zombie_toast").get(0).getPosition();
                Position exPos1 = new Position(1, 19);
                Position exPos2 = new Position(1, 21);
                Position exPos3 = new Position(0, 20);
                Position exPos4 = new Position(2, 20);

                // assert after movement
                Boolean onPortal = Pos.equals(exPos1) || Pos.equals(exPos2) || Pos.equals(exPos3) || Pos.equals(exPos4);
                assertTrue(onPortal);
        }

        @Test
        @DisplayName("Tests spawner interactions")
        public void testZombieSpawner() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("zombies",
                                "simpleNoSpiders");
                String spawnerId = getEntities(res, "zombie_toast_spawner").get(0).getId();
                assertThrows(InvalidActionException.class, () -> {
                        dmc.interact(spawnerId);
                });
                res = dmc.tick(Direction.DOWN);
                assertThrows(InvalidActionException.class, () -> {
                        dmc.interact(spawnerId);
                });
                res = dmc.tick(Direction.UP);
                assertDoesNotThrow(() -> {
                        dmc.interact(spawnerId);
                });
                res = dmc.getDungeonResponseModel();
                assert (res.getEntities().size() == 2);
                res = dmc.tick(Direction.DOWN);
                assert (res.getEntities().size() == 2);
                res = dmc.tick(Direction.DOWN);
                assert (res.getEntities().size() == 2);
                res = dmc.tick(Direction.DOWN);
                assert (res.getEntities().size() == 2);
                res = dmc.tick(Direction.DOWN);
                assert (res.getEntities().size() == 2);
        }

        @Test
        @DisplayName("Test the player can't move through double boulders")
        public void testBoulderBlock() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("d_staticTest_testBoulderBlock",
                                "c_movementTest_testMovementDown");
                EntityResponse initPlayer = getPlayer(res).get();

                // Player shouldn't move
                EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(),
                                initPlayer.getType(), new Position(1, 1), false);

                // move player downward
                res = dmc.tick(Direction.DOWN);
                EntityResponse actualPlayer = getPlayer(res).get();

                // assert after movement
                assertEquals(expectedPlayer, actualPlayer);

                // test other directions
                res = dmc.tick(Direction.UP);
                actualPlayer = getPlayer(res).get();
                assertEquals(expectedPlayer, actualPlayer);

                res = dmc.tick(Direction.LEFT);
                actualPlayer = getPlayer(res).get();
                assertEquals(expectedPlayer, actualPlayer);

                res = dmc.tick(Direction.RIGHT);
                actualPlayer = getPlayer(res).get();
                assertEquals(expectedPlayer, actualPlayer);
        }

        @Test
        @DisplayName("Test the player can't push boulders into walls")
        public void testBoulderWall() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("d_staticTest_testBoulderWall",
                                "c_movementTest_testMovementDown");
                EntityResponse initPlayer = getPlayer(res).get();

                // Player shouldn't move
                EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(),
                                initPlayer.getType(), new Position(1, 1), false);

                // move player downward
                res = dmc.tick(Direction.DOWN);
                EntityResponse actualPlayer = getPlayer(res).get();

                // assert after movement
                assertEquals(expectedPlayer, actualPlayer);

                // test other directions
                res = dmc.tick(Direction.UP);
                actualPlayer = getPlayer(res).get();
                assertEquals(expectedPlayer, actualPlayer);

                res = dmc.tick(Direction.LEFT);
                actualPlayer = getPlayer(res).get();
                assertEquals(expectedPlayer, actualPlayer);

                res = dmc.tick(Direction.RIGHT);
                actualPlayer = getPlayer(res).get();
                assertEquals(expectedPlayer, actualPlayer);
        }
        @Test
        @DisplayName("Test pushing")
        public void testBoulder() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("d_staticTest_testBoulder",
                                "c_movementTest_testMovementDown");
                EntityResponse initPlayer = getPlayer(res).get();

                EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(),
                                initPlayer.getType(), new Position(1, 2), false);

                // move player downward
                res = dmc.tick(Direction.DOWN);
                EntityResponse actualPlayer = getPlayer(res).get();

                // assert after movement
                assertEquals(expectedPlayer, actualPlayer);

                Position exBoulderPos = new Position(1, 3);
                Position boulderPos = getEntities(res, "boulder").get(0).getPosition();
                assertEquals(exBoulderPos, boulderPos);

                // test other directions
                res = dmc.tick(Direction.RIGHT);
                res = dmc.tick(Direction.DOWN);
                res = dmc.tick(Direction.LEFT);
                expectedPlayer = new EntityResponse(initPlayer.getId(),
                                initPlayer.getType(), new Position(1, 3), false);
                actualPlayer = getPlayer(res).get();
                assertEquals(expectedPlayer, actualPlayer);
                exBoulderPos = new Position(0, 3);
                boulderPos = getEntities(res, "boulder").get(0).getPosition();
                assertEquals(exBoulderPos, boulderPos);

                res = dmc.tick(Direction.DOWN);
                res = dmc.tick(Direction.LEFT);
                res = dmc.tick(Direction.UP);
                expectedPlayer = new EntityResponse(initPlayer.getId(),
                                initPlayer.getType(), new Position(0, 3), false);
                actualPlayer = getPlayer(res).get();
                assertEquals(expectedPlayer, actualPlayer);
                exBoulderPos = new Position(0, 2);
                boulderPos = getEntities(res, "boulder").get(0).getPosition();
                assertEquals(exBoulderPos, boulderPos);

                res = dmc.tick(Direction.LEFT);
                res = dmc.tick(Direction.UP);
                // Test repeated pushing
                res = dmc.tick(Direction.RIGHT);
                res = dmc.tick(Direction.RIGHT);
                expectedPlayer = new EntityResponse(initPlayer.getId(),
                                initPlayer.getType(), new Position(1, 2), false);
                actualPlayer = getPlayer(res).get();
                assertEquals(expectedPlayer, actualPlayer);
                exBoulderPos = new Position(2, 2);
                boulderPos = getEntities(res, "boulder").get(0).getPosition();
                assertEquals(exBoulderPos, boulderPos);
        }

        @Test
        @DisplayName("Test floor switch")
        public void testFloorSwitch() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("d_staticTest_testFloorSwitch",
                                "c_movementTest_testMovementDown");

                // assert floor switch off
                assertTrue(getGoals(res).contains(":boulders"));

                // move player downward into boulder
                res = dmc.tick(Direction.DOWN);

                // assert floor switch on
                assertFalse(getGoals(res).contains(":boulders"));

                // move player downward into boulder off of the switch
                res = dmc.tick(Direction.DOWN);

                // assert floor switch off again
                assertTrue(getGoals(res).contains(":boulders"));
        }

        @Test
        @DisplayName("Test multi floor switch")
        public void testFloorSwitchMulti() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("d_staticTest_testFloorSwitchMulti",
                                "c_movementTest_testMovementDown");

                // assert floor switch off
                assertTrue(getGoals(res).contains(":boulders"));

                // trigger switch 1
                res = dmc.tick(Direction.DOWN);

                // assert floor switch off
                assertTrue(getGoals(res).contains(":boulders"));

                // trigger switch 2
                res = dmc.tick(Direction.RIGHT);

                // assert floor switch on
                assertFalse(getGoals(res).contains(":boulders"));

                // untrigger switch 2
                res = dmc.tick(Direction.RIGHT);

                // assert floor switch off again
                assertTrue(getGoals(res).contains(":boulders"));
        }

        //
        // @Test
        // @DisplayName("Test exit")
        // public void useKeyWalkThroughOpenDoor() {
        // DungeonManiaController dmc;
        // dmc = new DungeonManiaController();
        // DungeonResponse res = dmc.newGame("d_DoorsKeysTest_exitPosition",
        // "c_DoorsKeysTest_useKeyWalkThroughOpenDoor");

        // // pick up key
        // res = dmc.tick(Direction.RIGHT);
        // //Position pos = getEntities(res, "player").get(0).getPosition();
        // Position pos1 = getEntities(res, "exit").get(0).getPosition();
        // assertEquals(1, getInventory(res, "exit").size());

        // walk through door and check key is gone= not working need to fixed
        // res = dmc.tick(Direction.RIGHT);
        // assertEquals(0, getInventory(res, "key").size());
        // assertNotEquals(pos, getEntities(res, "player").get(0).getPosition());
        // }

        // Checks if the boulder can be pushed by moving the player into a boulder
        // @Test
        // public void testBoulder() {
        // DungeonManiaController newDungeon = new DungeonManiaController();
        // DungeonResponse response;
        // DungeonResponse createNew = newDungeon.newGame("boulders",
        // "c_DoorsKeysTest");
        // //DungeonResponse createNew = newDungeon.newGame("boulders", "peaceful");
        // String playerId = helperMethod.getEntityId(new Position(2, 2, 3), createNew);
        // String boulder = helperMethod.getEntityId(new Position(3, 2, 1), createNew);
        // response = newDungeon.tick(Direction.RIGHT);
        // assertTrue(helperMethod.isEntityOnTile(response, new Position(3, 2, 3),
        // playerId));
        // assertTrue(helperMethod.isEntityOnTile(response, new Position(4, 2, 1),
        // boulder));
        // response = newDungeon.tick(Direction.RIGHT);
        // response = newDungeon.tick(Direction.RIGHT);
        // assertTrue(helperMethod.isEntityOnTile(response, new Position(4, 2, 3),
        // playerId));
        // assertTrue(helperMethod.isEntityOnTile(response, new Position(5, 2, 1),
        // boulder));
        // }
        // Checks if the door is working by unlocking a locked door with a key
        // @Test
        // public void testUnlockedDoor() {
        // DungeonManiaController newDungeon = new DungeonManiaController();
        // DungeonResponse response;
        // DungeonResponse createNew = newDungeon.newGame("door", "c_DoorsKeysTest");
        // String playerId = helperMethod.getEntityId(new Position(1, 1, 3), createNew);
        // response = newDungeon.tick(Direction.RIGHT);
        // response = newDungeon.tick(Direction.RIGHT);
        // assertTrue(helperMethod.isEntityOnTile(response, new Position(3, 1, 3),
        // playerId));
        // }

        // @Test
        // @DisplayName("Checks if the door is actually locked by moving towards the
        // door without a key")
        // public void testLockedDoor() {
        // DungeonManiaController newDungeon = new DungeonManiaController();
        // DungeonResponse response;
        // DungeonResponse createNew = newDungeon.newGame("d_DoorsKeysTest_lockedDoor",
        // "c_DoorsKeysTest");
        // String playerId = helperMethod.getEntityId(new Position(1, 1, 3), createNew);
        // response = newDungeon.tick(Direction.DOWN);
        // response = newDungeon.tick(Direction.RIGHT);
        // response = newDungeon.tick(Direction.RIGHT);
        // response = newDungeon.tick(Direction.RIGHT);
        // response = newDungeon.tick(Direction.UP);
        // assertTrue(helperMethod.isEntityOnTile(response, new Position(3, 2, 3),
        // playerId));
        // }
}
