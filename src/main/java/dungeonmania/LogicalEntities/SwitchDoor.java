package dungeonmania.LogicalEntities;

import dungeonmania.GameMap;
import dungeonmania.StaticEntities.Door;
import dungeonmania.util.Position;

public class SwitchDoor extends Door implements LogicalEntity{
    private boolean keyUnlocked = false;
    private boolean state = false;
    private Logic logic;
    
    public SwitchDoor(String id, String type, Position pos, int keyId, Logic logic) {
        super(id, type, pos, keyId);
        this.logic = logic;
    }

    public void setKeyUnlocked(boolean keyUnlocked) {
        this.keyUnlocked = keyUnlocked;
    }

    @Override
    public boolean getState() {
        return this.state;
    }

    @Override
    public void computeState(GameMap map) {
        if (!keyUnlocked) {
            this.state = logic.evaluateLogic(map, getPos());
        }
    }
    
}
