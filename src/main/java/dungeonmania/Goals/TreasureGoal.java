package dungeonmania.Goals;

import dungeonmania.GameMap;
import dungeonmania.response.models.ItemResponse;

/*
 * Defines the class for the treasure goal which is completed when a certain amount of treasure is collected
 */
public class TreasureGoal extends Goals {

    private GameMap map;
    private int goalCount;

    public TreasureGoal(GameMap map) {
        super();
        this.map = map;
        this.goalCount = map.getConfigs().get("treasure_goal").getAsInt();
    }

    public String getName() {
        return "treasure";
    }

    @Override
    public boolean isComplete() {
        // checking the amount of treasure collected and if it matches the goal amount
        int amount = 0;
        for (ItemResponse item : map.getInventory()) {
            if (item.getType().equals("treasure")) {
                amount++;
            }
        }
        return amount >= this.goalCount;
    }

    @Override
    public String formatString() {
        if (isComplete()) {
            return "";
        }
        int amount = 0;
        for (ItemResponse item : map.getInventory()) {
            if (item.getType().equals("treasure")) {
                amount++;
            }
        }
        return ":" + getName() + "(" + (this.goalCount - amount) + ")";
    }

}