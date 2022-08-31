package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;

import static dungeonmania.TestUtils.getValueFromConfigFile;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;

import dungeonmania.response.models.ItemResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;

public class BattleTests {
    // From Modified from ExampleTests.java
    private void assertBattleCalculationsAllyWeapons(String enemyType, BattleResponse battle, boolean enemyDies,
            String configFilePath, boolean withAlly) {
        List<RoundResponse> rounds = battle.getRounds();
        double playerHealth = Double.parseDouble(getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(getValueFromConfigFile(enemyType + "_attack", configFilePath));
        double allyAttack = 0;
        double allyDefence = 0;
        if (withAlly) {
            allyAttack = Double.parseDouble(getValueFromConfigFile("ally_attack", configFilePath));
            allyDefence = Double.parseDouble(getValueFromConfigFile("ally_defence", configFilePath));
        }

        for (RoundResponse round : rounds) {
            double swordAttack = 0;
            int bowMultiplier = 1;
            double shieldDef = 0;
            boolean isInvincible = false;
            for (ItemResponse item : round.getWeaponryUsed()) {
                if (item.getType().equals("sword")) {
                    swordAttack = Double.parseDouble(getValueFromConfigFile("sword_attack", configFilePath));
                } else if (item.getType().equals("bow")) {
                    bowMultiplier = 2;
                } else if (item.getType().equals("shield")) {
                    shieldDef = Double.parseDouble(getValueFromConfigFile("shield_defence", configFilePath));
                } else if (item.getType().equals("invincibility_potion")) {
                    isInvincible = true;
                }
            }

            if (isInvincible) {
                assertEquals(round.getDeltaCharacterHealth(), 0);
                assertEquals(round.getDeltaEnemyHealth(), -enemyHealth);
            } else {
                assertEquals(round.getDeltaCharacterHealth(), -(enemyAttack - allyDefence - shieldDef) / 10);
                assertEquals(round.getDeltaEnemyHealth(),
                        -(bowMultiplier * (playerAttack + swordAttack + allyAttack) / 5));
            }

            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();
        }

        if (enemyDies) {
            assertTrue(enemyHealth <= 0);
        } else {
            assertTrue(playerHealth <= 0);
        }
    }

    @Test
    @DisplayName("Test player sword breaks after battle")
    public void testSwordDurability() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest_swordDurability", "c_battleTests_swordDurability");

        // pick up sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "sword").size());

        // Walk over to fight Mercenary
        res = dmc.tick(Direction.RIGHT);
        BattleResponse battle = res.getBattles().get(0);
        // Mercenary dies
        assertBattleCalculationsAllyWeapons("mercenary", battle, true, "c_battleTests_swordDurability", false);

        // Should not have a sword anymore
        assertEquals(0, getInventory(res, "sword").size());
    }

    @Test
    @DisplayName("Test player bow breaks after battle")
    public void testBowDurability() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest_bowDurabilityTest", "c_battleTests_swordDurability");

        // pick up materials for bow
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertDoesNotThrow(() -> dmc.build("bow"));
        try {
            res = dmc.build("bow");
            // Check that materials are used and we have bow
            assertEquals(1, getInventory(res, "bow").size());
            assertEquals(0, getInventory(res, "arrow").size());
            assertEquals(0, getInventory(res, "wood").size());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvalidActionException e) {
            e.printStackTrace();
        }

        // Walk over to fight Mercenary
        res = dmc.tick(Direction.RIGHT);
        BattleResponse battle = res.getBattles().get(0);
        // Mercenary dies
        assertBattleCalculationsAllyWeapons("mercenary", battle, true, "c_battleTests_swordDurability", false);

        // Should not have a bow anymore
        assertEquals(0, getInventory(res, "bow").size());
    }

    @Test
    @DisplayName("Test player shield breaks after battle")
    public void testShieldDurability() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest_shieldDurability", "c_battleTests_swordDurability");

        // pick up materials for shield
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertDoesNotThrow(() -> dmc.build("shield"));
        try {
            res = dmc.build("shield");
            // Check that materials are used and we have bow
            assertEquals(0, getInventory(res, "wood").size());
            assertEquals(0, getInventory(res, "treasure").size());
            assertEquals(1, getInventory(res, "shield").size());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvalidActionException e) {
            e.printStackTrace();
        }

        // Walk over to fight Mercenary
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        BattleResponse battle = res.getBattles().get(0);
        // Mercenary dies
        assertBattleCalculationsAllyWeapons("mercenary", battle, true, "c_battleTests_swordDurability", false);

        // Should not have a shield anymore
        assertEquals(0, getInventory(res, "shield").size());
    }

    @Test
    @DisplayName("Tests Battle with an Ally")
    public void testBattleWithAlly() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest_AllyMercenary",
                "bribe_amount_3");

        String mercId = getEntities(res, "mercenary").get(0).getId();

        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        // get Ally
        assertDoesNotThrow(() -> {
            dmc.interact(mercId);
        });
        assert (getEntities(res, "mercenary").get(0).isInteractable() == false);
        assert (getInventory(res, "treasure").size() == 0);
        // Fight Mercenary with an ally
        res = dmc.tick(Direction.RIGHT);
        BattleResponse battle = res.getBattles().get(0);
        // Mercenary dies
        assertBattleCalculationsAllyWeapons("mercenary", battle, true, "bribe_amount_3", true);
    }

    @Test
    @DisplayName("Tests Battle while Invincible")
    public void testBattleInvincible() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest_Invincible",
                "c_battleTests_LongPotions");

        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assert (getInventory(res, "invincibility_potion").size() == 1);
        try {
            dmc.tick(getInventory(res, "invincibility_potion").get(0).getId());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidActionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Fight Mercenary with an Invinciblity
        // He runs away so we have to move till its trapped

        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        BattleResponse battle = res.getBattles().get(0);
        // Mercenary dies
        assertBattleCalculationsAllyWeapons("mercenary", battle, true, "c_battleTests_LongPotions", false);
    }

    @Test
    @DisplayName("Tests if battle triggers while invisibile")
    public void testNoBattleWhenInvisible() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest_Invisible",
                "c_battleTests_LongPotions");

        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assert (getInventory(res, "invisibility_potion").size() == 1);
        try {
            dmc.tick(getInventory(res, "invisibility_potion").get(0).getId());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidActionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertEquals(0, res.getBattles().size());
        // Should not fight Mercenary while invisible
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
    }

    @Test
    @DisplayName("Tests if battle triggers while invisible with ass")
    public void testBattleWhenInvisibleAss() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest_InvisibleAss",
                "c_battleTests_LongPotionsAss");

        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assert (getInventory(res, "invisibility_potion").size() == 1);
        try {
            dmc.tick(getInventory(res, "invisibility_potion").get(0).getId());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidActionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertEquals(1, res.getBattles().size());
        // Should not fight Mercenary while invisible
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, res.getBattles().size());
    }

    @Test
    @DisplayName("Tests whether an unkillable hydra remains alive")
    public void testFightInvincibleHydra() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("hydra",
                "M3HydraNeverDies");

        res = dmc.tick(Direction.RIGHT);
        BattleResponse battle = res.getBattles().get(0);
        assertEquals(10, battle.getRounds().size());
        assertEquals(1.0, battle.getRounds().get(0).getDeltaEnemyHealth());
    }
}
