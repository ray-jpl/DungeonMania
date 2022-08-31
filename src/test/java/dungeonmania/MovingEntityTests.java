package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static dungeonmania.TestUtils.getPlayer;
import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.InvalidActionException;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class MovingEntityTests {
        @Test
        @DisplayName("Test player can walk in all directions")
        public void testPlayerMovements() {
                // left right, up down, check all possible positon
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse initDungonRes = dmc.newGame("d_movementTest_testMovementDown",
                                "c_movementTest_testMovementDown");
                EntityResponse initPlayer = getPlayer(initDungonRes).get();

                // create the expected result
                EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(),
                                initPlayer.getType(), new Position(1, 2), false);

                // move player downward
                DungeonResponse actualDungonRes = dmc.tick(Direction.DOWN);
                EntityResponse actualPlayer = getPlayer(actualDungonRes).get();
                // assert after movement
                assertEquals(expectedPlayer, actualPlayer);

                // move player up
                expectedPlayer = new EntityResponse(initPlayer.getId(),
                                initPlayer.getType(), new Position(1, 1), false);
                actualDungonRes = dmc.tick(Direction.UP);
                actualPlayer = getPlayer(actualDungonRes).get();
                assertEquals(expectedPlayer, actualPlayer);

                // move player right
                expectedPlayer = new EntityResponse(initPlayer.getId(),
                                initPlayer.getType(), new Position(2, 1), false);
                actualDungonRes = dmc.tick(Direction.RIGHT);
                actualPlayer = getPlayer(actualDungonRes).get();
                assertEquals(expectedPlayer, actualPlayer);

                // move player left
                expectedPlayer = new EntityResponse(initPlayer.getId(),
                                initPlayer.getType(), new Position(1, 1), false);
                actualDungonRes = dmc.tick(Direction.LEFT);
                actualPlayer = getPlayer(actualDungonRes).get();
                assertEquals(expectedPlayer, actualPlayer);
        }

        @Test
        @DisplayName("Test player cannot walk through walls")
        public void testPlayerMovingIntoWall() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse initDungonRes = dmc.newGame("d_movementTest_testPlayerWall",
                                "c_movementTest_testMovementDown");
                EntityResponse initPlayer = getPlayer(initDungonRes).get();

                // create the expected result
                EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(),
                                initPlayer.getType(), new Position(1, 1), false);

                // move player Right but wall is on the right
                DungeonResponse actualDungonRes = dmc.tick(Direction.RIGHT);
                EntityResponse actualPlayer = getPlayer(actualDungonRes).get();
                // assert after movement that player is still on the same Position
                assertEquals(expectedPlayer, actualPlayer);
        }

        @Test
        @DisplayName("Tests Zombie Spawner Rate")
        public void testSpawnRate() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("zombies",
                                "simpleNoSpiders");

                // a number of entities should be four
                assert (res.getEntities().size() == 4);
                // check number of entities goes up as zombies spawn
                for (int i = 1; i < 50; i++) {
                        res = dmc.tick(Direction.UP);
                        assert (res.getEntities().size() == 4 + i / 10);
                }
        }

        @Test
        @DisplayName("Tests Zombie Spawner stuck between 4 walls")
        public void testSpawnStuck() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("spawnerStuck",
                                "simpleNoSpiders");

                // a number of entities should be four
                assert (res.getEntities().size() == 7);
                // check number of entities goes up as zombies spawn
                for (int i = 0; i < 50; i++) {
                        res = dmc.tick(Direction.UP);
                        assert (res.getEntities().size() == 7);
                }
        }

        @Test
        @DisplayName("Tests Zombie Spawner Rate Complex")
        public void testSpawnRateComplex() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("zombiesSpawnComplex",
                                "simpleNoSpiders");

                // a number of entities should be four
                assert (res.getEntities().size() == 12);
                // check number of entities goes up as zombies spawn
                for (int i = 0; i < 10; i++) {
                        res = dmc.tick(Direction.UP);
                }
                assert (res.getEntities().size() == 15);
        }

        @Test
        @DisplayName("Tests if zombie moves")
        public void testZombieMoves() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("oneZombie",
                                "simpleNoSpiders");

                Position pos = getEntities(res, "zombie_toast").get(0).getPosition();
                res = dmc.tick(Direction.UP);
                // zombie moved during the tick
                assertNotEquals(pos, getEntities(res, "zombie_toast").get(0).getPosition());
        }

        @Test
        @DisplayName("Tests if zombies get stuck")
        public void testZombieStuck() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("stuckZombie",
                                "simpleNoSpiders");

                Position pos1 = getEntities(res, "zombie_toast").get(0).getPosition();
                Position pos2 = getEntities(res, "zombie_toast").get(1).getPosition();
                res = dmc.tick(Direction.UP);
                // zombie did not move during the tick
                assertEquals(pos1, getEntities(res, "zombie_toast").get(0).getPosition());
                assertEquals(pos2, getEntities(res, "zombie_toast").get(1).getPosition());
        }

        @Test
        @DisplayName("Tests if mercenary moves correctly")
        public void testMercenaryMoves() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("oneMercenary",
                                "simpleNoSpiders");

                res = dmc.tick(Direction.UP);
                // mercenary moved during the tick
                assertEquals(new Position(1, 8), getEntities(res, "mercenary").get(0).getPosition());
                res = dmc.tick(Direction.DOWN);
                // mercenary moved during the tick
                assertEquals(new Position(1, 7), getEntities(res, "mercenary").get(0).getPosition());
        }

        @Test
        @DisplayName("Tests if mercenary moves around wall to get to player")
        public void testMercenaryMovesAroundWall() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("oneMercenaryWall",
                                "simpleNoSpiders");

                res = dmc.tick(Direction.UP);
                // mercenary moved during the tick
                assertEquals(new Position(3, 0), getEntities(res, "mercenary").get(0).getPosition());
                res = dmc.tick(Direction.DOWN);
                // mercenary moved during the tick
                assertEquals(new Position(2, 0), getEntities(res, "mercenary").get(0).getPosition());
        }

        @Test
        @DisplayName("Tests if mercenary gets stuck")
        public void testMercenaryStuck() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("stuckMercenary",
                                "simpleNoSpiders");

                Position pos1 = getEntities(res, "mercenary").get(0).getPosition();
                res = dmc.tick(Direction.UP);
                // mercenary did not move during the tick
                assertEquals(pos1, getEntities(res, "mercenary").get(0).getPosition());
        }

        @Test

        @DisplayName("Tests if spider moves correctly")
        public void testSpiderMoves() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("oneSpider",
                                "simpleNoSpiders");

                res = dmc.tick(Direction.UP);
                assertEquals(new Position(9, 8), getEntities(res,
                                "spider").get(0).getPosition());
                res = dmc.tick(Direction.DOWN);
                assertEquals(new Position(10, 8), getEntities(res,
                                "spider").get(0).getPosition());
                res = dmc.tick(Direction.UP);
                assertEquals(new Position(10, 9), getEntities(res,
                                "spider").get(0).getPosition());
                res = dmc.tick(Direction.DOWN);
                assertEquals(new Position(10, 10), getEntities(res,
                                "spider").get(0).getPosition());
                res = dmc.tick(Direction.UP);
                assertEquals(new Position(9, 10), getEntities(res,
                                "spider").get(0).getPosition());
                res = dmc.tick(Direction.DOWN);
                assertEquals(new Position(8, 10), getEntities(res,
                                "spider").get(0).getPosition());
                res = dmc.tick(Direction.UP);
                assertEquals(new Position(8, 9), getEntities(res,
                                "spider").get(0).getPosition());
                res = dmc.tick(Direction.DOWN);
                assertEquals(new Position(8, 8), getEntities(res,
                                "spider").get(0).getPosition());
                res = dmc.tick(Direction.UP);
                assertEquals(new Position(9, 8), getEntities(res,
                                "spider").get(0).getPosition());
                res = dmc.tick(Direction.DOWN);
                assertEquals(new Position(10, 8), getEntities(res,
                                "spider").get(0).getPosition());
        }

        @Test

        @DisplayName("Tests if spider moves correctly with boulders")
        public void testSpiderMovesBoulders() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("oneSpiderBoulder",
                                "simpleNoSpiders");

                res = dmc.tick(Direction.UP);
                assertEquals(new Position(9, 8), getEntities(res,
                                "spider").get(0).getPosition());
                res = dmc.tick(Direction.DOWN);
                assertEquals(new Position(10, 8), getEntities(res,
                                "spider").get(0).getPosition());
                res = dmc.tick(Direction.UP);
                assertEquals(new Position(9, 8), getEntities(res,
                                "spider").get(0).getPosition());
                res = dmc.tick(Direction.DOWN);
                assertEquals(new Position(8, 8), getEntities(res,
                                "spider").get(0).getPosition());
                res = dmc.tick(Direction.UP);
                assertEquals(new Position(8, 9), getEntities(res,
                                "spider").get(0).getPosition());
                res = dmc.tick(Direction.DOWN);
                assertEquals(new Position(8, 10), getEntities(res,
                                "spider").get(0).getPosition());
                res = dmc.tick(Direction.UP);
                assertEquals(new Position(9, 10), getEntities(res,
                                "spider").get(0).getPosition());
                res = dmc.tick(Direction.DOWN);
                assertEquals(new Position(10, 10), getEntities(res,
                                "spider").get(0).getPosition());
                res = dmc.tick(Direction.UP);
                assertEquals(new Position(9, 10), getEntities(res,
                                "spider").get(0).getPosition());
                res = dmc.tick(Direction.DOWN);
                assertEquals(new Position(8, 10), getEntities(res,
                                "spider").get(0).getPosition());
        }

        @Test

        @DisplayName("Tests if spiders spawn")
        public void testSpiderspawn() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("nothing",
                                "simpleSpidersSpawn");

                // a number of entities should be two
                assert (res.getEntities().size() == 2);
                // check number of entities goes up as spiders spawn
                for (int i = 1; i < 10; i++) {
                        res = dmc.tick(Direction.UP);
                        assert (res.getEntities().size() == 2 + i / 5);
                }
        }

        @Test
        @DisplayName("Tests if mercenaries can be bribes")
        public void testBribe() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("mercenary",
                                "bribe_amount_3");

                String mercId = getEntities(res, "mercenary").get(0).getId();
                assertThrows(InvalidActionException.class, () -> {
                        dmc.interact(mercId);
                });
                dmc.tick(Direction.RIGHT);
                dmc.tick(Direction.RIGHT);
                assertThrows(InvalidActionException.class, () -> {
                        dmc.interact(mercId);
                });
                dmc.tick(Direction.RIGHT);
                assertDoesNotThrow(() -> {
                        dmc.interact(mercId);
                });
                assert (getEntities(res, "mercenary").get(0).isInteractable() == false);
                assert (getInventory(res, "treasure").size() == 0);

        }

        @Test
        @DisplayName("Tests if assassin moves correctly")
        public void testAssassinMoves() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("oneAssassin",
                                "M3_configNoFail");

                res = dmc.tick(Direction.UP);
                // mercenary moved during the tick
                assertEquals(new Position(1, 8), getEntities(res, "assassin").get(0).getPosition());
                res = dmc.tick(Direction.DOWN);
                // mercenary moved during the tick
                assertEquals(new Position(1, 7), getEntities(res, "assassin").get(0).getPosition());
        }

        @Test
        @DisplayName("Tests if Assassin bribes properly")
        public void testBribeAssassin() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("assassin",
                                "M3_configNoFail");

                String assId = getEntities(res, "assassin").get(0).getId();
                assertThrows(InvalidActionException.class, () -> {
                        dmc.interact(assId);
                });
                dmc.tick(Direction.RIGHT);
                dmc.tick(Direction.RIGHT);
                dmc.tick(Direction.RIGHT);
                assertDoesNotThrow(() -> {
                        dmc.interact(assId);
                });
                assert (getEntities(res, "assassin").get(0).isInteractable() == false);
        }

        @Test
        @DisplayName("Tests if invincibility potion scares off entities")
        public void testInvincibleScares() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("invincibleScared",
                                "M3_configNoFail");
                res = dmc.tick(Direction.RIGHT);
                Position toastPos = getEntities(res, "zombie_toast").get(0).getPosition();
                Position hydraPos = getEntities(res, "hydra").get(0).getPosition();
                Position assassinPos = getEntities(res, "assassin").get(0).getPosition();
                Position mercPos = getEntities(res, "mercenary").get(0).getPosition();
                try {
                        dmc.tick(getInventory(res, "invincibility_potion").get(0).getId());
                } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (InvalidActionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                res = dmc.getDungeonResponseModel();
                assertEquals(getEntities(res, "zombie_toast").get(0).getPosition(),
                                new Position(toastPos.getX() + 1, toastPos.getY()));
                assertEquals(getEntities(res, "hydra").get(0).getPosition(),
                                new Position(hydraPos.getX() + 1, hydraPos.getY()));
                assertEquals(getEntities(res, "assassin").get(0).getPosition(),
                                new Position(assassinPos.getX() + 1, assassinPos.getY()));
                assertEquals(getEntities(res, "mercenary").get(0).getPosition(),
                                new Position(mercPos.getX() + 1, mercPos.getY()));
        }

        @Test

        @DisplayName("Tests when assassin bribe fails")
        public void testBribeAssFails() {
                DungeonManiaController dmc = new DungeonManiaController();
                DungeonResponse res = dmc.newGame("bribeAssFail",
                                "bribe_amount_3Ass");

                String mercId = getEntities(res, "assassin").get(0).getId();
                assertThrows(InvalidActionException.class, () -> {
                        dmc.interact(mercId);
                });
                dmc.tick(Direction.RIGHT);
                dmc.tick(Direction.RIGHT);
                assertThrows(InvalidActionException.class, () -> {
                        dmc.interact(mercId);
                });
                dmc.tick(Direction.RIGHT);
                assertDoesNotThrow(() -> {
                        dmc.interact(mercId);
                });
                assert (getInventory(res, "treasure").size() == 0);
        }
}
