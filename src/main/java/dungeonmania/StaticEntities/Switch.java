package dungeonmania.StaticEntities;

import dungeonmania.util.Position;
import dungeonmania.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import dungeonmania.GameMap;
import dungeonmania.LogicalEntities.LogicalEntity;
import dungeonmania.collectableEntities.Bomb.BombActive;
import dungeonmania.Observers.TickObserver;

public class Switch extends StaticEntity implements TickObserver, LogicalEntity{

    private boolean state;

    public Switch(String id, String type, Position pos) {
        super(id, type, pos);
        this.state = false;
    }

    public void update(int tick, GameMap map) {
        HashMap<Position,ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        if (!dungeonMap.containsKey(this.getPos())) {
            state = false;
            return;
        }
        for (Entity e: dungeonMap.get(this.getPos())) {
            if (e.getType().equals("boulder")) {
                state = true;
                // trigger bombs while in active state
                triggerBombs(map);
                updateAdjLogicalEntities(map);
                return;
            }
        }
        
        state = false;
        updateAdjLogicalEntities(map);
    }

    public boolean getState() {
        return state;
    }

    @Override
    public void computeState(GameMap map) {
        // State is only based on if it has a boulder on top
        // Should change it to the update function
        // However this implementation shouldnt be an issue for this milestone
        getState();
    }

    private void triggerBombs(GameMap map) {
        HashMap<Position,ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        List<Position> adjPos = this.getPos().getAdjacentPositions();
        for (int i = 1; i < 8; i += 2) {
            Position p = adjPos.get(i);
            if (dungeonMap.containsKey(p)) {
                for (Entity e : dungeonMap.get(p)) {
                    if (e.isType("active_bomb")) {
                        ((BombActive)e).explodeBomb(map);
                    }
                }
            }
        }
    }

    public void updateAdjLogicalEntities(GameMap map) {
        HashMap<Position,ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        List<Position> adjPos = this.getPos().getCardinallyAdjacentPositions();
        for (Position pos: adjPos) {
            if (dungeonMap.containsKey(pos)) {
                for (Entity e : dungeonMap.get(pos)) {
                    if (e.isType("wire") || e.isType("switch_door") || e.isType("light_bulb_off") || e.isType("light_bulb_on")) {
                        ((LogicalEntity) e).computeState(map);
                    }
                }
            }
        }
    }


}