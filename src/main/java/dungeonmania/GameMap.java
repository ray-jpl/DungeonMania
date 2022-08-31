package dungeonmania;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dungeonmania.Goals.AndGoal;
import dungeonmania.Goals.BouldersGoal;
import dungeonmania.Goals.EnemyGoal;
import dungeonmania.Goals.ExitGoal;
import dungeonmania.Goals.Goals;
import dungeonmania.Goals.OrGoal;
import dungeonmania.Goals.TreasureGoal;
import dungeonmania.MovingEntities.Assassin;
import dungeonmania.MovingEntities.Mercenary;
import dungeonmania.MovingEntities.OldPlayer;
import dungeonmania.MovingEntities.Player;
import dungeonmania.Observers.TickObserver;
import dungeonmania.StaticEntities.Door;
import dungeonmania.StaticEntities.Portal;
import dungeonmania.StaticEntities.SpiderSpawner;
import dungeonmania.StaticEntities.TimeTravellingPortal;
import dungeonmania.StaticEntities.ZombieSpawner;
import dungeonmania.collectableEntities.Key;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.other.Interactable;
import dungeonmania.response.models.AnimationQueue;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Position;

public class GameMap {
    private String dungeonId;
    private String dungeonName;
    private JsonObject configs;
    private List<EntityResponse> entities;
    private List<ItemResponse> inventory;
    private List<BattleResponse> battles;
    private List<String> buildables;
    private HashMap<Position, ArrayList<Entity>> dungeonMap;
    private List<HashMap<Position, ArrayList<Entity>>> states;
    private List<HashMap<String, Object>> actions;
    private List<AnimationQueue> animations;
    private Player player;
    private String goals;
    private Goals goalObj;

    private Position playerPos;
    private Position playerPrevPos;

    private int ticksPassed;
    private SpiderSpawner spiderSpawner = new SpiderSpawner(new Position(6, 6), 0);
    private ArrayList<TickObserver> tickObservers;

    public GameMap(String name, JsonArray entities, JsonObject goals, JsonObject configs) {
        this.dungeonId = UUID.randomUUID().toString();
        this.dungeonName = name;
        this.configs = configs;
        this.entities = new ArrayList<EntityResponse>();
        this.animations = new ArrayList<AnimationQueue>();
        // Copy of the Game map which is more editable
        this.dungeonMap = new HashMap<Position, ArrayList<Entity>>();

        EntityFactory entityFactory = new EntityFactory();
        this.tickObservers = new ArrayList<>();
        this.tickObservers.add((TickObserver) this.spiderSpawner);

        for (int i = 0; i < entities.size(); i++) {
            JsonObject entity = entities.get(i).getAsJsonObject();
            String id = UUID.randomUUID().toString();
            String type = entity.get("type").getAsString();
            Position position = new Position(entity.get("x").getAsInt(), entity.get("y").getAsInt());
            Boolean isInteractable = type == "zombie_toast_spawner" || type == "mercenary" || type == "assassin" ? true
                    : false;

            // Add entity to Response
            this.entities.add(new EntityResponse(id, type, position, isInteractable));

            // Add entity to the dungeonMap
            EntityInterface entityObject;
            if (type.equals("key") || type.equals("door")) {
                entityObject = EntityFactory.initialiseDoorAndKey(id, type, position, entity.get("key").getAsInt());
            } else if (type.equals("portal")) {
                entityObject = entityFactory.initialisePortal(id, type, position, entity.get("colour").getAsString());
            } else if (type.equals("light_bulb_off")) {
                entityObject = EntityFactory.initialiseLogicalEntity(id, type, position,
                        entity.get("logic").getAsString());
            } else if (type.equals("switch_door")) {
                entityObject = EntityFactory.initialiseLogicalEntity(id, type, position, entity.get("key").getAsInt(),
                        entity.get("logic").getAsString());
            } else {
                entityObject = entityFactory.initialiseEntity(id, type, position, configs);
            }
            if (type.equals("zombie_toast_spawner") || type.equals("switch")) {
                tickObservers.add((TickObserver) entityObject);
            }

            this.dungeonMap.computeIfAbsent(position, k -> new ArrayList<Entity>()).add((Entity) entityObject);

            // Record Player position for ease
            if (type.equals("player")) {
                this.playerPos = position;
                this.player = (Player) entityObject;
                this.tickObservers.add((TickObserver) entityObject);
            }
        }
        this.inventory = new ArrayList<ItemResponse>();
        this.battles = new ArrayList<BattleResponse>();
        this.buildables = new ArrayList<String>();
        this.ticksPassed = 0;
        this.states = new ArrayList<HashMap<Position, ArrayList<Entity>>>();
        this.actions = new ArrayList<HashMap<String, Object>>();
        this.spiderSpawner.setSpawnRate(configs.get("spider_spawn_rate").getAsInt());

        initialiseGoals(goals);
    }

    public HashMap<Position, ArrayList<Entity>> getDungeonMap() {
        return this.dungeonMap;
    }

    public String getDungeonId() {
        return this.dungeonId;
    }

    public String getDungeonName() {
        return this.dungeonName;
    }

    public String getGoals() {
        return this.goals;
    }

    public JsonObject getConfigs() {
        return this.configs;
    }

    public List<EntityResponse> getEntities() {
        return this.entities;
    }

    public List<ItemResponse> getInventory() {
        return this.inventory;
    }

    public List<BattleResponse> getBattles() {
        return this.battles;
    }

    public List<String> getBuildables() {
        return this.buildables;
    }

    public Position getPlayerPosition() {
        return this.playerPos;
    }

    public Position getPlayerPreviousPosition() {
        return this.playerPrevPos;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getTicksPassed() {
        return this.ticksPassed;
    }

    public void addAnimation(AnimationQueue aniQ) {
        this.animations.add(aniQ);
    }

    public Entity getEntity(String entityId) {
        Entity entity = null;
        for (ArrayList<Entity> entities : this.dungeonMap.values()) {
            for (Entity e : entities) {
                if (e.getId().equals(entityId)) {
                    entity = e;
                }
            }
        }
        return entity;
    }

    public void setDungeonMap(HashMap<Position, ArrayList<Entity>> dungeonMap) {

        // Need to sync the entity response list when we set the map

        List<EntityResponse> entities = new ArrayList<EntityResponse>();
        for (Position pos : this.dungeonMap.keySet()) {
            for (Entity entity : dungeonMap.get(pos)) {
                // TODO: Is there a better way to get the isInteractable attribute
                Boolean isInteractable = entity.isInteractable();
                // TODO: Probably can set the layer of each type of entity here as it gets
                // reinserted so it gets rendered correctly
                // Can probably introduce some type of helper method
                String entityType = entity.getType();
                if (entityType.equals("active_bomb")) {
                    entityType = "bomb";
                }
                entities.add(new EntityResponse(entity.getId(), entityType, pos, isInteractable));
            }
        }
        this.entities = entities;
    }

    public void setEntities(List<EntityResponse> entities) {
        this.entities = entities;
    }

    public void setInventory(List<ItemResponse> inventory) {
        this.inventory = inventory;
    }

    public void setBattles(List<BattleResponse> battles) {
        this.battles = battles;
    }

    public void setBuildables(List<String> buildables) {
        this.buildables = buildables;
    }

    public void setPlayerPosition(Position pos) {
        this.playerPos = pos;
    }

    public void setPlayerPrevPos() {
        this.playerPrevPos = this.playerPos;
    }

    // time travel
    public void addState(String command, Object detail) {
        HashMap<Position, ArrayList<Entity>> state = new HashMap<>();
        EntityFactory entityFactory = new EntityFactory();

        for (Position pos : dungeonMap.keySet()) {
            for (Entity entity : dungeonMap.get(pos)) {
                String id = entity.getId();
                String type = entity.getType();
                Position position = new Position(entity.getPos().getX(), entity.getPos().getY());

                // Add entity to the dungeonMap
                EntityInterface entityObject;
                if (type.equals("key")) {
                    Key temp = (Key) entity;
                    entityObject = EntityFactory.initialiseDoorAndKey(id, type, position, temp.getKeyId());
                } else if (type.equals("door")) {
                    Door temp = (Door) entity;
                    entityObject = EntityFactory.initialiseDoorAndKey(id, type, position, temp.getKeyId());
                } else if (type.equals("portal")) {
                    Portal temp = (Portal) entity;
                    entityObject = entityFactory.initialisePortal(id, type, position,
                            temp.getColour());
                } else {
                    entityObject = entityFactory.initialiseEntity(id, type, position, configs);
                }

                state.computeIfAbsent(position, k -> new ArrayList<Entity>()).add((Entity) entityObject);
            }
        }
        this.states.add(state);
        if (command.equals("")) {
            return;
        }
        HashMap<String, Object> action = new HashMap<>();
        action.put(command, detail);
        this.actions.add(action);
    }

    public void rewind(int ticks) {
        dungeonMap.clear();
        ArrayList<HashMap<String, Object>> acts = new ArrayList<>();
        int numOfInter = 0;
        for (int i = actions.size() - ticks; i < actions.size(); i++) {
            acts.add(actions.get(i));
            for (String com : actions.get(i).keySet()) {
                if (com.equals("interact")) {
                    numOfInter++;
                }
            }
        }
        for (Position pos : states.get(states.size() - ticks - numOfInter - 1).keySet()) {
            for (Entity entity : states.get(states.size() - ticks - numOfInter - 1).get(pos)) {
                dungeonMap.computeIfAbsent(pos, k -> new ArrayList<>()).add(entity);
                if (entity instanceof Player) {
                    dungeonMap.get(pos).add(new OldPlayer(UUID.randomUUID().toString(), "older_player", pos,
                            this.player.getMaxHealth(), this.player.getAttack(), acts));
                    dungeonMap.get(pos).remove(entity);
                    dungeonMap.computeIfAbsent(player.getPos(), k -> new ArrayList<>()).add(player);
                }
            }
        }

        this.player.useTimeTurner(this);
        setDungeonMap(dungeonMap);
    }

    public void clearAnimations() {
        this.animations.clear();
    }

    private void initialiseGoals(JsonObject goalObj) {
        String goal = goalObj.get("goal").getAsString();
        Goals goalObject;
        if (goal.equals("AND")) {
            goalObject = new AndGoal(this, goalObj.getAsJsonArray("subgoals").get(0).getAsJsonObject(),
                    goalObj.getAsJsonArray("subgoals").get(1).getAsJsonObject());
        } else if (goal.equals("OR")) {
            goalObject = new OrGoal(this, goalObj.getAsJsonArray("subgoals").get(0).getAsJsonObject(),
                    goalObj.getAsJsonArray("subgoals").get(1).getAsJsonObject());
        } else if (goal.equals("enemies")) {
            goalObject = new EnemyGoal(this);
        } else if (goal.equals("boulders")) {
            goalObject = new BouldersGoal(this);
        } else if (goal.equals("treasure")) {
            goalObject = new TreasureGoal(this);
        } else if (goal.equals("exit")) {
            goalObject = new ExitGoal(this);
        } else {
            return;
        }
        this.goalObj = goalObject;
        this.goals = goalObject.formatString();
    }

    public void updateGoals() {
        this.goals = this.goalObj.formatString();
    }

    public DungeonResponse toDungeonResponse() {
        return new DungeonResponse(this.dungeonId, this.dungeonName, this.entities, this.inventory, this.battles,
                this.buildables, this.goals, this.animations);
    }

    public void updateTick() {
        this.ticksPassed++;
        for (TickObserver obs : this.tickObservers) {
            obs.update(this.ticksPassed, this);
        }
    }

    public void removeTickObserver(TickObserver tickObs) {
        this.tickObservers.remove(tickObs);
    }

    public void interact(String entityId) throws InvalidActionException, IllegalArgumentException {
        Entity entity = getEntity(entityId);
        if (entity == null || !(entity instanceof Interactable)) {
            throw new IllegalArgumentException();
        }
        Interactable entityInteractable = (Interactable) entity;
        entityInteractable.interact(this);
    }

    public void oldInteract(String entityId) throws InvalidActionException, IllegalArgumentException {
        Entity entity = getEntity(entityId);
        if (entity == null || !(entity instanceof Interactable)) {
            throw new IllegalArgumentException();
        }
        Interactable entityInteractable = (Interactable) entity;
        entityInteractable.oldInteract(this);
    }

    public void timePortal() {
        if (!dungeonMap.containsKey(player.getPos())) {
            return;
        }
        for (Entity entity : dungeonMap.get(player.getPos())) {
            if (entity instanceof TimeTravellingPortal) {
                entity.interact(this, player);
            }
        }
    }
}
