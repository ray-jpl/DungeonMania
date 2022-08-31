package dungeonmania.StaticEntities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import dungeonmania.Entity;
import dungeonmania.GameMap;
import dungeonmania.MovingEntities.ZombieToast;
import dungeonmania.Observers.TickObserver;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.other.Interactable;

public class ZombieSpawner extends StaticEntity implements TickObserver, Interactable {
    private Position spawn;
    private int spawnRate;

    public ZombieSpawner(String id, String type, Position spawn, int spawnRate) {
        super(id, type, spawn);
        this.spawn = spawn;
        this.spawnRate = spawnRate;
    }

    public void update(int tick, GameMap map) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        if (spawnRate == 0 || tick == 0) {
            return;
        } else if (tick % spawnRate == 0) {
            Position spawnPos = emptyCardinalSquare(spawn, dungeonMap);
            if (!spawnPos.equals(this.spawn)) {
                Entity zombie_toast = new ZombieToast(UUID.randomUUID().toString(), "zombie_toast", spawnPos,
                        map.getConfigs().get("zombie_health").getAsInt(),
                        map.getConfigs().get("zombie_attack").getAsInt());
                dungeonMap.computeIfAbsent(spawnPos, k -> new ArrayList<Entity>()).add(zombie_toast);
                map.setDungeonMap(dungeonMap);
            }
        }
    }

    public Position emptyCardinalSquare(Position spawn, HashMap<Position, ArrayList<Entity>> dungeonMap) {
        Position pos = new Position(spawn.getX(), spawn.getY());
        if (!dungeonMap.keySet().contains(pos.translateBy(Direction.UP))) {
            return pos.translateBy(Direction.UP);
        } else if (!dungeonMap.keySet().contains(pos.translateBy(Direction.RIGHT))) {
            return pos.translateBy(Direction.RIGHT);
        } else if (!dungeonMap.keySet().contains(pos.translateBy(Direction.DOWN))) {
            return pos.translateBy(Direction.DOWN);
        } else if (!dungeonMap.keySet().contains(pos.translateBy(Direction.LEFT))) {
            return pos.translateBy(Direction.LEFT);
        } else {
            return pos;
        }
    }

    public void interact(GameMap map) throws InvalidActionException {

        if (!Position.isAdjacent(this.getPos(), map.getPlayerPosition())) {
            throw new InvalidActionException("Not cardinally next to spawner");
        } else if (!map.getInventory().stream()
                .anyMatch(item -> item.getType().equals("sword") || item.getType().equals("bow"))) {
            throw new InvalidActionException("No weapon");
        }

        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        dungeonMap.get(this.getPos()).remove(this);
        if (dungeonMap.get(this.getPos()).size() == 0) {
            dungeonMap.remove(this.getPos());
        }
        map.removeTickObserver(this);

        map.setDungeonMap(dungeonMap);
    }

    public void oldInteract(GameMap map) throws InvalidActionException {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        dungeonMap.get(this.getPos()).remove(this);
        if (dungeonMap.get(this.getPos()).size() == 0) {
            dungeonMap.remove(this.getPos());
        }
        map.removeTickObserver(this);

        map.setDungeonMap(dungeonMap);
    }

    @Override
    public boolean isInteractable() {
        return true;
    }
}
