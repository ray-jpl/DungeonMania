package dungeonmania.Goals;

import dungeonmania.GameMap;

/*
 * Defines the class for the goal of killing a certain number of enemies 
 */
public class EnemyGoal extends Goals {

    private GameMap map;
    private int goalCount;

    public EnemyGoal(GameMap map) {
        super();
        this.map = map;
        this.goalCount = map.getConfigs().get("enemy_goal").getAsInt();

    }

    public String getName() {
        return "enemies";
    }

    @Override
    public boolean isComplete() {
        if (this.map.getPlayer().enemiesKilled() >= this.goalCount) {
            return true;
        }
        return false;

    }

    @Override
    public String formatString() {
        if (isComplete()) {
            return "";
        }
        return ":" + getName() + "(" + (this.goalCount - this.map.getPlayer().enemiesKilled()) + ")";
    }

}
