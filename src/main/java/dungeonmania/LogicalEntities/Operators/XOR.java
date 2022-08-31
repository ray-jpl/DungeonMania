package dungeonmania.LogicalEntities.Operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dungeonmania.Entity;
import dungeonmania.GameMap;
import dungeonmania.LogicalEntities.Logic;
import dungeonmania.LogicalEntities.LogicalEntity;
import dungeonmania.util.Position;

public class XOR implements Logic{

    @Override
    public boolean evaluateLogic(GameMap map, Position position) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        List<Position> adjPos = position.getCardinallyAdjacentPositions();
        int activeAdjacents = 0;

        for (Position pos: adjPos) {
            if (dungeonMap.containsKey(pos)) {
                for (Entity e : dungeonMap.get(pos)) {
                    if (e.isType("switch") || e.isType("wire")) {
                        if (((LogicalEntity)e).getState()) {
                            activeAdjacents += 1;
                        }
                    } 
                }
            }
        }

        if (activeAdjacents == 1) {
            return true;
        }

        return false;
    }
    
}
