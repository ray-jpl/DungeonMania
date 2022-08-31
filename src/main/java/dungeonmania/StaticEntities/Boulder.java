package dungeonmania.StaticEntities;

import dungeonmania.util.Position;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import dungeonmania.GameMap;
import dungeonmania.Entity;
import dungeonmania.MovingEntities.MovingEntity;

public class Boulder extends StaticEntity {

    /**
     * Constructor for Boulder
     * 
     * @param id
     * @param type
     * @param pos
     */
    public Boulder(String id, String type, Position pos) {
        super(id, type, pos);
    }

    public void interact(GameMap map, MovingEntity source) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        Boolean playerSource = false;
        for (Entity y : dungeonMap.get(this.getPos())) {
            if (y.getType().equals("player")) {
                playerSource = true;
            }
        }
        if (!playerSource) {
            return;
        }

        Position prevPlayerPos = map.getPlayerPreviousPosition();

        List<Position> adjPos = this.getPos().getAdjacentPositions();

        int i = adjPos.indexOf(prevPlayerPos);
        i = (i + 4) % 8;
        Position targetPos = adjPos.get(i);
        // check target position has boulder
        if (dungeonMap.containsKey(targetPos)
                && dungeonMap.get(targetPos).stream().anyMatch(x -> x.isType("boulder") || x.isType("wall"))) {
            source.move(map, prevPlayerPos);
            return;
        }
        if (dungeonMap.containsKey(this.getPos())) {
            dungeonMap.get(this.getPos()).remove(this);
            if (dungeonMap.get(this.getPos()).size() == 0) {
                dungeonMap.remove(this.getPos());
            }
        }
        dungeonMap.computeIfAbsent(targetPos, k -> new ArrayList<>()).add(this);
        this.setPos(targetPos);
        map.setDungeonMap(dungeonMap);
    }

}
