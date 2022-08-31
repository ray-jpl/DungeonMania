package dungeonmania.LogicalEntities.Operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dungeonmania.Entity;
import dungeonmania.GameMap;
import dungeonmania.LogicalEntities.Logic;
import dungeonmania.LogicalEntities.LogicalEntity;
import dungeonmania.util.Position;

public class AND implements Logic{

    @Override
    public boolean evaluateLogic(GameMap map, Position position) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        List<Position> adjPos = position.getCardinallyAdjacentPositions();
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
                        activeAdjacents += 1;
                    }
                }
            }
        }

        if (switchCount > 2) {
            return switchCount == activeSwitch ? true : false;
        } else if (activeAdjacents >= 2) {
            return true;
        }

        return false;
        
    }

    
}
