package dungeonmania.collectableEntities.Bomb;

import java.util.ArrayList;
import java.util.HashMap;

import dungeonmania.Entity;
import dungeonmania.GameMap;
import dungeonmania.MovingEntities.Player;
import dungeonmania.util.Position;

public class BombActive extends Entity{
    private int bombRadius;
    public BombActive(String id, String type, Position pos, int bombRadius) {
        super(id, type, pos);
        this.bombRadius = bombRadius;
    }

    public int getBombRadius() {
        return this.bombRadius;
    }

    public void explodeBomb(GameMap map) {
        // remove every entity from top left to bottom right of the radius
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        
        Position centre = this.getPos();
        // Get top left of radius
        Position topLeft = centre.translateBy(-getBombRadius(),-getBombRadius());
        for (int x = 0; x <= 2*getBombRadius(); x++) {
            for(int y = 0; y <= 2*getBombRadius(); y++) {
                Position pointer = topLeft.translateBy(x,y);
                if (dungeonMap.get(pointer) != null) {
                    ArrayList<Entity> entities = dungeonMap.get(pointer);
                    Player player = null;
                    System.out.println(entities);
                    for (Entity entity: entities) {
                        if (entity instanceof Player) {
                            player = (Player) entity;
                            break;
                        }
                    }
                    dungeonMap.remove(pointer);
                    if (player != null) {
                        // Add player back in after blast
                        dungeonMap.computeIfAbsent(pointer, k -> new ArrayList<Entity>()).add((Entity) player);
                    }
                }
                
            }
        }
        map.setDungeonMap(dungeonMap);
    }
}
