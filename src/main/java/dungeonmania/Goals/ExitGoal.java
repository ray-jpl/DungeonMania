package dungeonmania.Goals;

import dungeonmania.Entity;
import dungeonmania.GameMap;
import java.util.ArrayList;
import java.util.HashMap;
import dungeonmania.util.Position;

/*
 * Defines the class for the exit goal where the game is complete once the exit goal is completed
 */
public class ExitGoal extends Goals {
    private GameMap map;
    private Goals siblingGoal;

    public ExitGoal(GameMap map) {
        super();
        this.map = map;
    }

    public String getName() {
        return "exit";
    }

    public void setSiblingGoal(Goals siblingGoal) {
        this.siblingGoal = siblingGoal;
    }

    @Override
    public boolean isComplete() {
        // Sibling goal must be completed
        if (siblingGoal != null && !siblingGoal.isComplete()) {
            return false;
        }

        HashMap<Position,ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        for (ArrayList<Entity> x: dungeonMap.values()) {
            for (Entity y: x) {
                if (y.getType().equals("exit") && y.getPos().equals(map.getPlayerPosition())) {
                    return true;
                }
            }
        }
        return false;

    }

    public String formatString() {
        if (isComplete()) {
            return "";
        }
        return ":" + getName();
    }

}
