package dungeonmania.LogicalEntities;

import dungeonmania.GameMap;
import dungeonmania.util.Position;

public interface Logic {
    public boolean evaluateLogic(GameMap map, Position pos);
}
