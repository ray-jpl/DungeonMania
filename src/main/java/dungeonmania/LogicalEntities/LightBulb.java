package dungeonmania.LogicalEntities;

import dungeonmania.GameMap;
import dungeonmania.StaticEntities.StaticEntity;
import dungeonmania.util.Position;

public class LightBulb extends StaticEntity implements LogicalEntity{
    private boolean state;
    private Logic logic;
    public LightBulb(String id, String type, Position pos, Logic logic) {
        super(id, type, pos);
        this.state = false;
        this.logic = logic;
    }
    @Override
    public boolean getState() {
        return this.state;
    }
    @Override
    public void computeState(GameMap map) {
        this.state = logic.evaluateLogic(map, getPos());
        if (state) {
            this.setType("light_bulb_on");
        } else {
            this.setType("light_bulb_off");
        }
        // update bulb
        map.setDungeonMap(map.getDungeonMap());

        
    }
    
}
