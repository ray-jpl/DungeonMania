package dungeonmania.LogicalEntities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dungeonmania.Entity;
import dungeonmania.GameMap;
import dungeonmania.LogicalEntities.Operators.OR;
import dungeonmania.StaticEntities.StaticEntity;
import dungeonmania.util.Position;

public class Wire extends StaticEntity implements LogicalEntity{
    private boolean state;
    // Assuming wires should just follow OR logic, if neighbours are activated then it should be as well
    private Logic logic = new OR();
    public Wire(String id, String type, Position pos) {
        super(id, type, pos);
    }

    @Override
    public boolean getState() {
        return this.state;
    }

    @Override
    public void computeState(GameMap map) {
        boolean prevState = this.state;
        this.state = logic.evaluateLogic(map, getPos());
        
        if (this.state != prevState) {
            updateAdjLogicalEntities(map);
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
