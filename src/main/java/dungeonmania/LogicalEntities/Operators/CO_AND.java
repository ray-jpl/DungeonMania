package dungeonmania.LogicalEntities.Operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dungeonmania.Entity;
import dungeonmania.GameMap;
import dungeonmania.LogicalEntities.Logic;
import dungeonmania.LogicalEntities.LogicalEntity;
import dungeonmania.util.Position;

public class CO_AND implements Logic{
    private int prevActive = 0;

    public int getPrevActive() {
        return prevActive;
    }

    public void setPrevActive(int prev) {
        this.prevActive = prev;
    }

    @Override
    public boolean evaluateLogic(GameMap map, Position position) {
        // Method is similar to AND logic
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        List<Position> adjPos = position.getCardinallyAdjacentPositions();
        int prevActiveTick = getPrevActive();
        int switchCount = 0;
        int activeSwitch = 0;
        int activeAdjacents = 0;


        for (Position pos: adjPos) {
            if (dungeonMap.containsKey(pos)) {
                for (Entity e : dungeonMap.get(pos)) {
                    if (e.isType("switch")) {
                        switchCount += 1;
                        if (((LogicalEntity)e).getState()) {
                            activeSwitch += 1;
                            activeAdjacents += 1;
                        }
                    } else if (e.isType("wire")) {
                        ((LogicalEntity)e).computeState(map);
                        if (((LogicalEntity)e).getState()) {
                            activeAdjacents += 1;
                        }
                        
                    }
                }
            }
        }

        setPrevActive(activeAdjacents);
        if (prevActiveTick != 0) {
            return false;
        }

        if (switchCount > 2) {
            return switchCount == activeSwitch ? true : false;
        } else if (activeAdjacents >= 2) {
            return true;
        }

        return false;

    }
    
}
