package dungeonmania.MovingEntities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import dungeonmania.Entity;
import dungeonmania.EntityFactory;
import dungeonmania.EntityInterface;
import dungeonmania.GameMap;
import dungeonmania.LogicalEntities.SwitchDoor;
import dungeonmania.Observers.TickObserver;

import dungeonmania.buildableEntities.BuildableHelper;
import dungeonmania.collectableEntities.CollectableEntity;
import dungeonmania.collectableEntities.Key;
import dungeonmania.collectableEntities.Bomb.Bomb;
import dungeonmania.collectableEntities.Potions.Potion;
import dungeonmania.response.models.AnimationQueue;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.StaticEntities.Door;
import dungeonmania.StaticEntities.TimeTravellingPortal;

public class Player extends MovingEntity implements TickObserver {
    private ArrayList<Entity> inventory;
    private ArrayList<Potion> effects;
    private int enemiesKilled;
    private double originalHealth;

    public Player(String id, String type, Position pos, double health, double attack) {
        super(id, type, pos, health, attack);
        this.inventory = new ArrayList<Entity>();
        this.effects = new ArrayList<Potion>();
        this.addUntraversable("wall");
        this.addUntraversable("active_bomb");
        this.addUntraversable("portal");
        this.enemiesKilled = 0;
        this.originalHealth = health;
    }

    public Entity getItemByType(String type) {
        for (Entity entity : this.inventory) {
            if (entity.getType().equals(type)) {
                return entity;
            }
        }
        return null;
    }

    public double getHealthDecimal() {
        return this.getHealth() / this.originalHealth;
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
        // update gameMap inventory
        updateGameMapInventory(map);
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

    public void removeItem(GameMap map, String id) {
        for (Entity entity : this.inventory) {
            if (entity.getId().equals(id)) {
                this.inventory.remove(entity);
                updateGameMapInventory(map);
                break;
            }
        }
    }

    public void useItem(GameMap map, String itemUsedId) {
        Entity item = findItem(map, itemUsedId);
        if (item instanceof Bomb) {
            Bomb bomb = (Bomb) item;
            Position playerPos = map.getPlayerPosition();
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
        updateGameMapInventory(map);
    }

    public void updateBuildables(GameMap map) {
        // Check what is buildable with the new picked up item
        List<String> buildablesList = new ArrayList<>();
        BuildableHelper.buildableObjects().stream().forEach(x -> {
            if (isBuildable(x)) {
                buildablesList.add(x);
            }
        });
        // Update gameMap with buildables
        map.setBuildables(buildablesList);
    }

    public void updateGameMapInventory(GameMap map) {
        List<ItemResponse> updatedInventory = new ArrayList<>();
        this.inventory.stream().forEach(e -> updatedInventory.add(new ItemResponse(e.getId(), e.getType())));
        map.setInventory(updatedInventory);
        updateBuildables(map);
    }

    public boolean isBuildable(String buildable) {
        boolean isPossible = false;
        // Only need to check if object is buildable do not need to retrieve the actual
        // objects
        HashMap<String, Integer> inventoryCopy = new HashMap<>();

        // Populate hashmap with frequency pair of objects
        this.inventory.stream().forEach(e -> {
            inventoryCopy.putIfAbsent(e.getType(), 0);
            inventoryCopy.put(e.getType(), inventoryCopy.get(e.getType()) + 1);
        });

        List<HashMap<String, Integer>> blueprints = BuildableHelper.buildableBlueprints(buildable);
        for (HashMap<String, Integer> blueprint : blueprints) {
            isPossible = true;
            // Check if all items in the blueprint are available
            for (String item : blueprint.keySet()) {
                if (!inventoryCopy.containsKey(item) || inventoryCopy.get(item) < blueprint.get(item)) {
                    isPossible = false;
                    // Dont break here as there may be other blueprints to check
                }
            }
            if (isPossible) {
                return true;
            }
        }

        return isPossible;
    }

    // Obtain Buildable is only called when isBuildable is true
    // Hence no checking if sufficient materials exist is necessary
    public void obtainBuildable(GameMap map, String buildable) {

        List<HashMap<String, Integer>> blueprints = BuildableHelper.buildableBlueprints(buildable);

        ArrayList<Entity> buffer = new ArrayList<>();

        for (HashMap<String, Integer> materialsList : blueprints) {
            for (String material : materialsList.keySet()) {
                // Search Inventory for correct number of material
                for (int i = 0; i < materialsList.get(material); i++) {
                    for (Entity e : this.inventory) {
                        if (e.getType().equals(material) && !buffer.contains(e)) {
                            buffer.add(e);
                        }
                    }
                }
            }
        }

        // At this point then we have the sufficient materials
        // Remove from our inventory
        buffer.stream().forEach(e -> this.inventory.remove(e));

        EntityFactory entityFactory = new EntityFactory();
        Entity item = (Entity) entityFactory.initialiseBuildableEntity(UUID.randomUUID().toString(), buildable, null,
                map.getConfigs());

        this.inventory.add(item);
        updateGameMapInventory(map);
    }

    public void move(GameMap map, Direction direction) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        Position playerPos = map.getPlayerPosition();
        Position prevPos = map.getPlayerPosition();
        dungeonMap.get(playerPos).remove(this);
        if (dungeonMap.get(playerPos).size() == 0) {
            dungeonMap.remove(playerPos);
        }

        Position newPos = playerPos.translateBy(direction);

        if (dungeonMap.containsKey(newPos)
                && dungeonMap.get(newPos).stream().anyMatch(x -> (x instanceof Door) && ((x.getType().equals("door")||x.getType().equals("switch_door") )))) {
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
                        updateGameMapInventory(map);
                        // Set this to an unlocked Door
                        if (entity.isType("switch_door")) {
                            SwitchDoor sDoor = (SwitchDoor) entity;
                            sDoor.setKeyUnlocked(true);
                        } else {
                            entity.setType("door_open");
                        }
                        
                    } else if (entity.isType("switch_door")) {
                        SwitchDoor sDoor = (SwitchDoor) entity;
                        if (sDoor.getState()) {
                            move(map, newPos);
                            playerPos = newPos;
                        } else {
                            moveToSamePos(map, playerPos);
                        }
                    }else {
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
                if (!(entity instanceof TimeTravellingPortal)) {
                    entity.interact(map, this);
                }
            }
        }
    }

    public Boolean move(GameMap map, Position newPos) {
        Boolean result = super.move(map, newPos);
        map.setPlayerPrevPos();
        map.setPlayerPosition(this.getPos());
        return result;
    }

    public void moveToSamePos(GameMap map, Position playerPos) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        map.setPlayerPrevPos();
        map.setPlayerPosition(playerPos);
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

    public double getMaxHealth() {
        return super.getHealth();
    }

    @Override
    public void move(GameMap map) {
        // TODO Auto-generated method stub

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

    public int treasureAmount() {
        int amount = 0;
        for (Entity item : this.inventory) {
            if (item.getType().equals("treasure")) {
                amount++;
            }
        }
        return amount;
    }

    public void useTreasure(GameMap map, int amount) {
        while (amount > 0) {
            for (Entity item : this.inventory) {
                if (item.getType().equals("treasure")) {
                    this.inventory.remove(item);
                    amount--;
                    break;
                }
            }
        }
        updateGameMapInventory(map);
    }

    public void useTimeTurner(GameMap map) {
        for (Entity item : this.inventory) {
            if (item.getType().equals("time_turner")) {
                this.inventory.remove(item);
                break;
            }
        }
        updateGameMapInventory(map);
    }

    public int enemiesKilled() {
        return this.enemiesKilled;
    }

    public void incrementEnemiesKilled() {
        this.enemiesKilled++;
    }
}
