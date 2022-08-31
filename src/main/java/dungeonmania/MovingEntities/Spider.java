package dungeonmania.MovingEntities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import dungeonmania.Entity;
import dungeonmania.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Spider extends MovingEntity implements Hostile{
    private final int RESET = 8;
    private Boolean middle;
    private Boolean clockwise;
    // 7 0 1
    // 6 # 2
    // 5 4 3
    private ArrayList<Direction> directions = new ArrayList<>();
    private ArrayList<Direction> directionsReverse = new ArrayList<>();
    private int currentPos;

    public Spider(String id, String type, Position pos, double health, double attack) {
        super(id, type, pos, health, attack);
        this.addUntraversable("boulder");
        this.middle = true;
        this.clockwise = true;
        Direction[] array = { Direction.RIGHT, Direction.DOWN, Direction.DOWN, Direction.LEFT, Direction.LEFT,
                Direction.UP, Direction.UP, Direction.RIGHT };
        Collections.addAll(this.directions, array);
        Direction[] array2 = { Direction.LEFT, Direction.LEFT, Direction.UP, Direction.UP, Direction.RIGHT,
                Direction.RIGHT, Direction.DOWN, Direction.DOWN };
        Collections.addAll(this.directionsReverse, array2);
        this.currentPos = 0;
    }

    public void move(GameMap map) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        Position pos = this.getPos();
        dungeonMap.get(pos).remove(this);
        if (dungeonMap.get(pos).size() == 0) {
            dungeonMap.remove(pos);
        }
        Position newPos = calculateSpiderPosition(pos);
        ///// TODO: Implement when we implement non movable entities
        // ArrayList<Entity> newPositionEntities = dungeonMap.get(newPos);
        // if (!newPositionEntities.stream().anyMatch(entity -> entity instanceof
        ///// ImmovableEntity)) {
        // playerPos = newPos;
        // }
        if (dungeonMap.containsKey(newPos)
                && dungeonMap.get(newPos).stream().anyMatch(x -> this.getUntraversable().contains(x.getType()))) {
            this.clockwise = !this.clockwise;
            newPos = calculateSpiderPosition(newPos);
            newPos = calculateSpiderPosition(newPos);
            if (dungeonMap.containsKey(newPos)
                    && dungeonMap.get(newPos).stream().anyMatch(x -> this.getUntraversable().contains(x.getType()))) {
                newPos = pos;
            }

        }
        dungeonMap.computeIfAbsent(newPos, k -> new ArrayList<>()).add(this);
        this.setPos(newPos);

        // Update map
        map.setDungeonMap(dungeonMap);

        if (dungeonMap.containsKey(newPos)) {
            for (Entity entity : dungeonMap.get(newPos)) {
                // Call entity specific action (portal)
                entity.interact(map, this);
            }
        }
    }

    public Position calculateSpiderPosition(Position pos) {
        Direction direction;
        if (this.middle) {
            direction = Direction.UP;
            this.middle = false;
        } else if (clockwise) {
            direction = this.directions.get(this.currentPos);
            this.currentPos++;
        } else {
            direction = this.directionsReverse.get(this.currentPos);
            this.currentPos--;
        }
        this.currentPos = this.currentPos < 0 ? this.currentPos + RESET : this.currentPos;
        this.currentPos = this.currentPos >= RESET ? this.currentPos - RESET : this.currentPos;

        return pos.translateBy(direction);
    }
}
