package dungeonmania.LogicalEntities.Operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dungeonmania.GameMap;
import dungeonmania.LogicalEntities.Logic;
import dungeonmania.LogicalEntities.LogicalEntity;
import dungeonmania.util.Position;
import dungeonmania.Entity;

public class OR implements Logic{
    @Override
    public boolean evaluateLogic(GameMap map, Position position) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        List<Position> adjPos = position.getCardinallyAdjacentPositions();
        
        for (Position pos: adjPos) {
            if (dungeonMap.containsKey(pos)) {
                for (Entity e : dungeonMap.get(pos)) {
                    if (e.isType("wire") || e.isType("switch") ) {
                        return ((LogicalEntity) e).getState();
                    }
                }
            }
        }
        return false;
    }
    
}
