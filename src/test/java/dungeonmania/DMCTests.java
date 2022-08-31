package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertThrows;



import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;


public class DMCTests {
    @Test
    @DisplayName("Tests newGame() exceptions")
    public void testNewGameExceptions() {
        DungeonManiaController dmc = new DungeonManiaController();
        assertThrows(IllegalArgumentException.class, () -> dmc.newGame("ddsadsdds", "c_movementTest_testMovementDown"));
        assertThrows(IllegalArgumentException.class, () -> dmc.newGame("d_movementTest_testMovementDown", "ccccccccc"));
    }

    @Test
    @DisplayName("Basic newgame test to check if it returns a proper response")
    public void testNewGameBasic() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_movementTest_testMovementDown",
                "c_movementTest_testMovementDown");
        assert initDungonRes.getEntities().size() == 2;
        assert initDungonRes.getInventory().size() == 0;
        assert initDungonRes.getBattles().size() == 0;
        assert initDungonRes.getBuildables().size() == 0;
        assert initDungonRes.getGoals().contains(":exit");
    }

}
