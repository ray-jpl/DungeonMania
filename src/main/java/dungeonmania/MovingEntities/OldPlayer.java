package dungeonmania.MovingEntities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dungeonmania.Entity;
import dungeonmania.EntityFactory;
import dungeonmania.EntityInterface;
import dungeonmania.GameMap;
import dungeonmania.Observers.TickObserver;

import dungeonmania.collectableEntities.CollectableEntity;
import dungeonmania.collectableEntities.Key;
import dungeonmania.collectableEntities.Bomb.Bomb;
import dungeonmania.collectableEntities.Potions.Potion;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.StaticEntities.Door;

public class OldPlayer extends MovingEntity implements TickObserver, Hostile {
    private ArrayList<Entity> inventory;
    private ArrayList<Potion> effects;
    private List<HashMap<String, Object>> actions;

    public OldPlayer(String id, String type, Position pos, double health, double attack,
            List<HashMap<String, Object>> actions) {
        super(id, type, pos, health, attack);
        this.inventory = new ArrayList<Entity>();
        this.effects = new ArrayList<Potion>();
        this.addUntraversable("wall");
        this.addUntraversable("active_bomb");
        this.addUntraversable("portal");
        this.actions = actions;
    }

    public void pickupItem(GameMap map, Position curPos, Entity item) {
        // If item is a key and we already have a key
        if (item.getType().equals("key") && this.inventory.stream().anyMatch(e -> e.getType().equals("key"))) {
            return;
        }

        this.inventory.add(item);
        // Remove the item from the map
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        dungeonMap.get(curPos).remove(item);
        map.setDungeonMap(dungeonMap);
    }

    public Entity findItem(GameMap map, String itemUsedId) {
        Entity item = null;
        for (Entity entity : this.inventory) {
            if (entity.getId().equals(itemUsedId)) {
                item = entity;
                return item;
            }
        }
        return item;
    }

    public void useItem(GameMap map, String itemUsedId) {
        Entity item = findItem(map, itemUsedId);
        if (item instanceof Bomb) {
            Bomb bomb = (Bomb) item;
            Position playerPos = this.getPos();
            HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();

            EntityInterface activeBomb = EntityFactory.initialiseActiveBomb(bomb.getId(), "active_bomb", playerPos,
                    bomb.getBombRadius());

            dungeonMap.get(playerPos).add((Entity) activeBomb);
            map.setDungeonMap(dungeonMap);

        } else if (item instanceof Potion) {
            HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
            this.effects.add((Potion) item);
            map.setDungeonMap(dungeonMap);
        }
        this.inventory.remove(item);
    }

    public void move(GameMap map, Direction direction) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        Position playerPos = this.getPos();
        dungeonMap.get(playerPos).remove(this);
        if (dungeonMap.get(playerPos).size() == 0) {
            dungeonMap.remove(playerPos);
        }

        Position newPos = playerPos.translateBy(direction);

        if (dungeonMap.containsKey(newPos)
                && dungeonMap.get(newPos).stream().anyMatch(x -> (x instanceof Door) && (x.getType().equals("door")))) {
            for (Entity entity : dungeonMap.get(newPos)) {
                if (entity instanceof Door) {
                    Door door = (Door) entity;
                    int keyId = door.getKeyId();
                    if (getKeyInInventory() != null && getKeyInInventory().getKeyId() == keyId) {
                        // If we have correct key
                        move(map, newPos);
                        playerPos = newPos;
                        // Remove key
                        this.inventory.remove((Entity) getKeyInInventory());
                        // Set this to an unlocked Door
                        entity.setType("door_open");
                    } else {
                        // Incorrect Key or no key means stay in same place
                        moveToSamePos(map, playerPos);
                    }
                    break;
                }
            }
        } else if (dungeonMap.containsKey(newPos)
                && dungeonMap.get(newPos).stream().anyMatch(x -> this.getUntraversable().contains(x.getType()))) {
            moveToSamePos(map, playerPos);
        } else {
            move(map, newPos);
            playerPos = newPos;
        }

        // Update map
        map.setDungeonMap(dungeonMap);

        // Check if we can pickup if an item exists
        for (Entity entity : dungeonMap.get(playerPos)) {
            if (entity instanceof CollectableEntity) {
                this.pickupItem(map, playerPos, entity);
            }
        }

        if (dungeonMap.containsKey(newPos)) {
            for (Entity entity : dungeonMap.get(newPos)) {
                // Call entity specific action (portal)
                entity.interact(map, this);
            }
        }
    }

    public Boolean move(GameMap map, Position newPos) {
        Boolean result = super.move(map, newPos);
        return result;
    }

    public void moveToSamePos(GameMap map, Position playerPos) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        dungeonMap.computeIfAbsent(playerPos, k -> new ArrayList<>()).add(this);
    }

    // find the keyId in Inventroy
    public Key getKeyInInventory() {
        Key key = null;
        for (Entity item : this.inventory) {
            if (item instanceof Key) {
                key = (Key) item;
            }
        }
        return key;
    }

    @Override
    public void move(GameMap map) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        HashMap<String, Object> action = actions.get(0);
        actions.remove(0);
        String command = null;
        for (String act : action.keySet()) {
            command = act;
        }

        while (command.equals("interact")) {
            try {
                map.oldInteract((String) action.get(command));

            } catch (Exception e) {
            }
            action = actions.get(0);
            actions.remove(0);
            for (String act : action.keySet()) {
                command = act;
            }
        }

        if (command.equals("tickMove")) {
            move(map, (Direction) action.get(command));
        } else if (command.equals("tickItem")) {
            useItem(map, (String) action.get(command));
        }
        if (actions.size() == 0) {
            dungeonMap.get(this.getPos()).remove(this);
            map.setDungeonMap(dungeonMap);
        }
    }

    public void update(int tick, GameMap map) {
        if (this.effects.size() != 0) {
            this.effects.get(0).tick();
            if (this.effects.get(0).getDuration() <= 0) {
                this.effects.remove(0);
            }
        }
    }

    public String getCurrEffect() {
        if (this.effects.size() == 0) {
            return null;
        } else {
            return this.effects.get(0).getType();
        }
    }

    public Potion getCurrPotion() {
        if (this.effects.size() == 0) {
            return null;
        } else {
            return this.effects.get(0);
        }
    }
}
