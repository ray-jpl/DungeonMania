package dungeonmania.LogicalEntities;

import dungeonmania.GameMap;

public interface LogicalEntity {
    public boolean getState();
    public void computeState(GameMap map);
}
