package dungeonmania.StaticEntities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dungeonmania.util.Position;
import dungeonmania.GameMap;
import dungeonmania.Entity;
import dungeonmania.MovingEntities.MovingEntity;

public class Portal extends StaticEntity {

    private String colour;

    public Portal(String id, String type, Position pos, String colour) {
        super(id, type, pos);
        this.colour = colour;
    }

    public String getColour() {
        return colour;
    }

    public void interact(GameMap map, MovingEntity target) {
        // Entity blacklist
        if (target.isType("zombie_toast") || target.isType("spider")) {
            return;
        }
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        Entity portal = null;
        // Find the linked portal entity
        for (ArrayList<Entity> x : dungeonMap.values()) {
            for (Entity y : x) {
                if (y.getType().equals("portal") && y.getPos() != this.getPos()
                        && ((Portal) y).getColour().equals(this.colour)) {
                    portal = y;
                }
            }
        }

        // Check neighboring positions clockwise and move target to first available
        List<Position> adjPos = portal.getPos().getAdjacentPositions();
        for (int i = 1; i < 8; i += 2) {
            if (dungeonMap.containsKey(adjPos.get(i))) {
                if (dungeonMap.get(adjPos.get(i)).stream().anyMatch(x -> x.isType("wall"))) {
                    continue;
                }
            }
            // tp here
            target.move(map, adjPos.get(i));
            return;
        }
    }
}
