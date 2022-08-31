package dungeonmania.MovingEntities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dungeonmania.Entity;
import dungeonmania.GameMap;
import dungeonmania.StaticEntities.Portal;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public abstract class MovingEntity extends Entity {
    private double health;
    private double attack;
    private final double INFINITY = 10000000000.0;
    private final int MAX_RECON = 10;
    private ArrayList<String> untraversable = new ArrayList<>();

    public MovingEntity(String id, String type, Position pos, double health, double attack) {
        super(id, type, pos);
        this.health = health;
        this.attack = attack;
    }

    public double getHealth() {
        return this.health;
    }

    public double getAttack() {
        return this.attack;
    }

    public void setHealth(double newHealth) {
        this.health = newHealth;
    }

    public void addUntraversable(String entity) {
        this.untraversable.add(entity);
    }

    public ArrayList<String> getUntraversable() {
        return this.untraversable;
    }

    public abstract void move(GameMap map);

    /*
     * Teleports entity to pos
     * returns false if not successful
     */
    public Boolean move(GameMap map, Position newPos) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        Position pos = this.getPos();
        if (dungeonMap.containsKey(pos)) {
            dungeonMap.get(pos).remove(this);
            if (dungeonMap.get(pos).size() == 0) {
                dungeonMap.remove(pos);
            }
        }
        if (dungeonMap.containsKey(newPos)) {
            for (Entity entity : dungeonMap.get(newPos)) {
                // Call entity specific action (portal)
                if (entity instanceof Portal) {
                    entity.interact(map, this);
                    if (this instanceof Player) {
                        map.setPlayerPrevPos();
                        map.setPlayerPosition(this.getPos());
                    }
                    return !this.getPos().equals(pos);
                }
            }
        }

        if (dungeonMap.containsKey(newPos)
                && dungeonMap.get(newPos).stream().anyMatch(x -> this.untraversable.contains(x.getType()))) {
            dungeonMap.computeIfAbsent(pos, k -> new ArrayList<>()).add(this);
            this.setPos(pos);
            map.setDungeonMap(dungeonMap);
            return false;
        } else {
            dungeonMap.computeIfAbsent(newPos, k -> new ArrayList<>()).add(this);
            this.setPos(newPos);
            map.setDungeonMap(dungeonMap);
            return true;
        }
    }

    public void randomMove(GameMap map) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        Position pos = this.getPos();
        dungeonMap.get(pos).remove(this);
        if (dungeonMap.get(pos).size() == 0) {
            dungeonMap.remove(pos);
        }
        Direction direction;
        int randomNum = (int) (Math.random() * 4);
        if (randomNum == 0) {
            direction = Direction.UP;
        } else if (randomNum == 1) {
            direction = Direction.LEFT;
        } else if (randomNum == 2) {
            direction = Direction.DOWN;
        } else {
            direction = Direction.RIGHT;
        }

        Position newPos = pos.translateBy(direction);
        if (dungeonMap.containsKey(newPos)
                && dungeonMap.get(newPos).stream().anyMatch(x -> this.untraversable.contains(x.getType()))) {
            dungeonMap.computeIfAbsent(pos, k -> new ArrayList<>()).add(this);
            this.setPos(pos);
        } else {
            dungeonMap.computeIfAbsent(newPos, k -> new ArrayList<>()).add(this);
            this.setPos(newPos);
        }

        // Update map
        map.setDungeonMap(dungeonMap);
    }

    public void moveToPlayer(GameMap map) {
        if (this.dijkstras(map) != null && !this.getPos().equals(map.getPlayerPosition())) {
            this.move(map, this.dijkstras(map));
        } else if (this.toPlayer(map, map.getPlayerPreviousPosition()) != this.getPos()) {
            this.move(map, this.toPlayer(map, map.getPlayerPreviousPosition()));
        }
    }

    public void moveAwayFromPlayer(GameMap map) {
        if (this.fromPlayer(map, map.getPlayerPreviousPosition()) != this.getPos()) {
            this.move(map, this.fromPlayer(map, map.getPlayerPreviousPosition()));
        }
    }

    public Position toPlayer(GameMap map, Position player) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        Position currPos = this.getPos();
        List<Position> cardinalPos = currPos.getCardinallyAdjacentPositions();
        double min = INFINITY;
        for (Position pos : cardinalPos) {
            if (Position.distanceBetween(pos, player) < min && !(dungeonMap.containsKey(pos)
                    && dungeonMap.get(pos).stream()
                            .anyMatch(k -> this.untraversable.contains(k.getType())))) {
                min = Position.distanceBetween(pos, player);
                currPos = pos;
            }
        }
        return currPos;
    }

    public Position fromPlayer(GameMap map, Position player) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        Position currPos = this.getPos();
        List<Position> cardinalPos = currPos.getCardinallyAdjacentPositions();
        double max = -1.0;
        for (Position pos : cardinalPos) {
            if (Position.distanceBetween(pos, player) > max && !(dungeonMap.containsKey(pos)
                    && dungeonMap.get(pos).stream()
                            .anyMatch(k -> this.untraversable.contains(k.getType())))) {
                max = Position.distanceBetween(pos, player);
                currPos = pos;
            }
        }
        return currPos;
    }

    public Position dijkstras(GameMap map) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        Position currPos = this.getPos();
        Position playerPos = map.getPlayerPosition();
        HashMap<Position, Double> dist = new HashMap<>();
        HashMap<Position, Position> prev = new HashMap<>();
        ArrayList<Position> queue = new ArrayList<>();
        for (int x = currPos.getX() - MAX_RECON; x < currPos.getX() + MAX_RECON; x++) {
            for (int y = currPos.getY() - MAX_RECON; y < currPos.getY() + MAX_RECON; y++) {
                Position pos = new Position(x, y);
                dist.put(pos, INFINITY);
                prev.put(pos, null);
                queue.add(pos);
            }
        }

        dist.replace(currPos, 0.0);
        Position min_pos = currPos;
        while (!queue.isEmpty()) {
            double min_dist = INFINITY;
            for (Position pos : queue) {
                if (dist.get(pos) < min_dist) {
                    min_dist = dist.get(pos);
                    min_pos = pos;
                }
            }
            if (min_dist == INFINITY) {
                break;
            }
            queue.remove(min_pos);
            for (Position pos : min_pos.getCardinallyAdjacentPositions()) {
                if (!queue.contains(pos)) {
                    continue;
                } else if (dungeonMap.containsKey(pos)
                        && dungeonMap.get(pos).stream()
                                .anyMatch(k -> this.untraversable.contains(k.getType()))
                        && queue.contains(pos)) {
                    queue.remove(pos);
                } else if (dist.get(min_pos) + 1 < dist.get(pos)) {
                    dist.replace(pos, dist.get(min_pos) + 1);
                    prev.replace(pos, min_pos);
                }
                if (pos.equals(playerPos)) {
                    break;
                }
            }
        }
        if (!prev.containsKey(playerPos) || dist.get(playerPos) == INFINITY) {
            return null;
        }

        Position pos = playerPos;
        while (prev.get(pos) != null && !prev.get(pos).equals(currPos)) {
            pos = prev.get(pos);
        }

        return pos;
    }
}
