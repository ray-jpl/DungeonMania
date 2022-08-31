package dungeonmania.Goals;

import dungeonmania.StaticEntities.Switch;
import dungeonmania.Entity;
import dungeonmania.GameMap;
import java.util.ArrayList;
import java.util.HashMap;
import dungeonmania.util.Position;

/*
 * Define the class for the boulder goal where the goal is complete when the boulder is on the floor switch
 */
public class BouldersGoal extends Goals {

    private GameMap map;

    public BouldersGoal(GameMap map) {
        super();
        this.map = map;
    }

    public String getName() {
        return "boulders";
    }

    @Override
    public boolean isComplete() {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        // filter to get only floorswitch and check if they are all switched
        for (ArrayList<Entity> x : dungeonMap.values()) {
            for (Entity y : x) {
                if (y.getType().equals("switch") && !((Switch) y).getState()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String formatString() {
        if (isComplete()) {
            return "";
        }
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        int switches = 0;
        int boulders = 0;
        int switchesPressed = 0;
        // filter to get only floorswitch and check if they are all switched
        for (ArrayList<Entity> x : dungeonMap.values()) {
            for (Entity y : x) {
                if (y.getType().equals("switch")) {
                    switches++;
                    if (((Switch) y).getState()) {
                        switchesPressed++;
                    }
                } else if (y.getType().equals("boulder")) {
                    boulders++;
                }
            }
        }
        return ":" + getName() + "(" + (boulders - switchesPressed) + ")/:switch(" + +(switches - switchesPressed)
                + ")";
    }

}
