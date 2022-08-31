package dungeonmania;

import dungeonmania.MovingEntities.Assassin;
import dungeonmania.MovingEntities.Hostile;
import dungeonmania.MovingEntities.MovingEntity;
import dungeonmania.MovingEntities.Player;
import dungeonmania.buildableEntities.BuildableHelper;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;

import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DungeonManiaController {
    private GameMap map;

    public String getSkin() {
        return "default";
    }

    public String getLocalisation() {
        return "en_US";
    }

    /**
     * /dungeons
     */
    public static List<String> dungeons() {
        return FileLoader.listFileNamesInResourceDirectory("dungeons");
    }

    /**
     * /configs
     */
    public static List<String> configs() {
        return FileLoader.listFileNamesInResourceDirectory("configs");
    }

    /**
     * /game/new
     */
    public DungeonResponse newGame(String dungeonName, String configName) throws IllegalArgumentException {
        JsonArray entities;
        JsonObject goals;
        JsonObject configs;

        try {
            String file = FileLoader.loadResourceFile("/dungeons/" + dungeonName + ".json");
            JsonObject dungeon = JsonParser.parseString(file).getAsJsonObject();
            entities = dungeon.getAsJsonArray("entities");
            goals = dungeon.getAsJsonObject("goal-condition");
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        try {
            String file = FileLoader.loadResourceFile("/configs/" + configName + ".json");
            JsonObject config = JsonParser.parseString(file).getAsJsonObject();
            configs = config.getAsJsonObject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal Argument");
        }
        this.map = new GameMap(dungeonName, entities, goals, configs);
        map.addState("", "");
        return map.toDungeonResponse();
    }

    /**
     * /game/dungeonResponseModel
     */
    public DungeonResponse getDungeonResponseModel() {
        return map.toDungeonResponse();
    }

    /**
     * /game/tick/item
     */
    public DungeonResponse tick(String itemUsedId) throws IllegalArgumentException, InvalidActionException {
        map.clearAnimations();
        // TODO: Refactor how to check for valid items?
        List<String> validItems = new ArrayList<>();
        validItems.add("bomb");
        validItems.add("invincibility_potion");
        validItems.add("invisibility_potion");
        DungeonResponse drm = getDungeonResponseModel();

        // Check if Item exists in Inventory
        if (!drm.getInventory().stream().anyMatch(item -> item.getId().equals(itemUsedId))) {
            throw new InvalidActionException(itemUsedId);
        }

        if (!drm.getInventory().stream()
                .anyMatch(item -> (item.getId().equals(itemUsedId) && validItems.contains(item.getType())))) {
            throw new IllegalArgumentException();
        }

        Player player = getPlayer(map);
        player.useItem(map, itemUsedId);

        // moves non-player entities

        moveEntities();
        if (isBattle(map)) {
            Battle battle = new Battle();
            map.getBattles().add(battle.battleStart(map));
        }

        map.updateTick();
        map.addState("tickItem", itemUsedId);
        map.updateGoals();
        return map.toDungeonResponse();
    }

    /**
     * /game/tick/movement
     */
    public DungeonResponse tick(Direction movementDirection) {
        map.clearAnimations();
        // Player moves first
        Player player = getPlayer(map);
        player.move(map, movementDirection);
        if (isBattle(map)) {
            Battle battle = new Battle();
            List<BattleResponse> battles = map.getBattles();
            battles.add(battle.battleStart(map));
            map.setBattles(battles);
        }

        // moves non-player entities
        moveEntities();

        if (isBattle(map)) {
            Battle battle = new Battle();
            List<BattleResponse> battles = map.getBattles();
            battles.add(battle.battleStart(map));
            map.setBattles(battles);
        }
        // needs to come last so that switch events (bombs/goal) trigger on the same
        // tick
        map.updateTick();
        map.addState("tickMove", movementDirection);
        map.timePortal();
        map.updateGoals();

        return map.toDungeonResponse();
    }

    /**
     * /game/build
     */
    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        if (!BuildableHelper.buildableObjects().contains(buildable)) {
            throw new IllegalArgumentException();
        }

        Player player = getPlayer(map);

        if (player.isBuildable(buildable)) {
            player.obtainBuildable(map, buildable);
        } else {
            throw new InvalidActionException("Insufficient materials");
        }

        return map.toDungeonResponse();
    }

    /**
     * /game/interact
     */
    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        ArrayList<String> validInteractions = new ArrayList<>();
        validInteractions.add("zombie_toast_spawner");
        validInteractions.add("mercenary");
        validInteractions.add("assassin");
        DungeonResponse drm = getDungeonResponseModel();
        if (!drm.getEntities().stream().anyMatch(
                entity -> (entity.getId().equals(entityId) && validInteractions.contains(entity.getType())))) {
            throw new IllegalArgumentException();
        }
        map.interact(entityId);
        map.addState("interact", entityId);
        map.updateGoals();
        return map.toDungeonResponse();
    }

    /**
     * Helper function for moving entities
     */
    public void moveEntities() {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        ArrayList<MovingEntity> entities = new ArrayList<>();

        for (ArrayList<Entity> list : dungeonMap.values()) {
            for (Entity entity : list) {
                if (entity instanceof MovingEntity && !(entity instanceof Player)) {
                    entities.add((MovingEntity) entity);
                }
            }
        }
        for (MovingEntity entity : entities) {
            entity.move(map);
        }
    }

    /**
     * /game/save
     */
    public DungeonResponse saveGame(String name) throws IllegalArgumentException {
        return null;
    }

    /**
     * /game/load
     */
    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        return null;
    }

    /**
     * /games/all
     */
    public List<String> allGames() {
        return new ArrayList<>();
    }

    /**
     * Extension Task 2
     * 
     * @param xStart
     * @param yStart
     * @param xEnd
     * @param yEnd
     * @param configName
     * @return
     */
    public DungeonResponse generateDungeon(int xStart, int yStart, int xEnd, int yEnd, String configName) {
        JsonArray entities = new JsonArray();
        JsonObject goals;
        JsonObject configs;

        try {
            String file = FileLoader.loadResourceFile("/configs/" + configName + ".json");
            JsonObject config = JsonParser.parseString(file).getAsJsonObject();
            configs = config.getAsJsonObject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal Argument");
        }

        // Initialise Exit Goal
        goals = new JsonObject();
        goals.addProperty("goal", "exit");

        // Initialise the walls, player and exit into a JsonArray for GameMap to process

        // Initialise the border enclosure
        for (int i = xStart - 1; i <= xEnd + 1; i++) {
            for (int j = yStart - 1; j <= yEnd + 1; j++) {
                if (i == xStart - 1 || i == xEnd + 1 || j == yStart - 1 || j == yEnd + 1) {
                    JsonObject wall = new JsonObject();
                    wall.addProperty("type", "wall");
                    wall.addProperty("x", i);
                    wall.addProperty("y", j);
                    entities.add(wall);
                }
            }
        }

        // Initialise the maze
        int width = xEnd - xStart + 1;
        int height = yEnd - yStart + 1;
        boolean[][] maze = randomisedPrims(width, height);
        for (int i = xStart; i <= xEnd; i++) {
            for (int j = yStart; j <= yEnd; j++) {
                int xCoord = i - xStart;
                int yCoord = j - yStart;
                if (maze[yCoord][xCoord] == false) {
                    JsonObject wall = new JsonObject();
                    wall.addProperty("type", "wall");
                    wall.addProperty("x", i);
                    wall.addProperty("y", j);
                    entities.add(wall);
                }
            }
        }

        // Initialise the player
        JsonObject player = new JsonObject();
        player.addProperty("type", "player");
        player.addProperty("x", xStart);
        player.addProperty("y", yStart);
        entities.add(player);

        // Initialise the Exit
        JsonObject exit = new JsonObject();
        exit.addProperty("type", "exit");
        exit.addProperty("x", xEnd);
        exit.addProperty("y", yEnd);
        entities.add(exit);
        // Use configName as the dungeon name since the name does not affect how the
        // dungeon functions
        this.map = new GameMap(configName, entities, goals, configs);
        return map.toDungeonResponse();
    }

    /**
     * Extension 1 Task
     * 
     * @param ticks
     * @return
     */
    public DungeonResponse rewind(int ticks) throws IllegalArgumentException {
        if (ticks <= 0 || ticks > map.getTicksPassed()) {
            throw new IllegalArgumentException("Invalid ticks");
        }
        map.rewind(ticks);
        return map.toDungeonResponse();
    }

    /*
     * Helper function to retrieve player object from map
     * 
     * @param map
     * 
     * @return Player
     */
    public Player getPlayer(GameMap map) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        ArrayList<Entity> playerPosition = dungeonMap.get(map.getPlayerPosition());
        // Occurs if player died
        if (playerPosition == null) {
            return null;
        }
        Player player = null;
        for (Entity entity : playerPosition) {
            if (entity instanceof Player) {
                player = (Player) entity;
                return player;
            }
        }
        return player;
    }

    public boolean isBattle(GameMap map) {
        Position playerPos = map.getPlayerPosition();
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        Player player = getPlayer(map);

        if (player != null && player.getCurrEffect() != null) {
            if (player.getCurrEffect().equals("invisibility_potion")
                    && !dungeonMap.get(playerPos).stream().anyMatch(x -> x instanceof Assassin)) {
                return false;
            }
        }

        // player died
        if (player == null) {
            return false;
        }
        return dungeonMap.get(playerPos).stream().anyMatch(x -> x instanceof Hostile);
    }

    // Randomised Prims for Extension 2 Task
    public boolean[][] randomisedPrims(int width, int height) {
        Position start = new Position(0, 0);
        Position end = new Position(width - 1, height - 1);

        boolean[][] maze = new boolean[height][width];
        // False represents a wall and true is empty space
        for (boolean[] row : maze) {
            Arrays.fill(row, false);
        }

        maze[start.getY()][start.getX()] = true;

        ArrayList<Position> optionList = options(start, width, height, maze, false);

        while (optionList.size() > 0) {
            int index = (int) (Math.random() * optionList.size());
            Position next = optionList.remove(index);

            ArrayList<Position> neighbours = options(next, width, height, maze, true);
            if (neighbours.size() > 0) {
                int neighbourIndex = (int) (Math.random() * neighbours.size());
                Position neighbour = neighbours.remove(neighbourIndex);

                maze[next.getY()][next.getX()] = true;
                Position inbetween = Position.calculateCoordBetweenAdjacent(next, neighbour);
                maze[inbetween.getY()][inbetween.getX()] = true;
                maze[neighbour.getY()][neighbour.getX()] = true;
            }

            optionList.addAll(options(next, width, height, maze, false));
        }
        // at the end there is still a case where our end position isn't connected to
        // the map
        // we don't necessarily need this, you can just keep randomly generating maps
        // (was original intention)
        // but this will make it consistently have a pathway between the two.
        if (maze[end.getY()][end.getX()] == false) {
            maze[end.getY()][end.getX()] = true;

            List<Position> endAdjPos = end.getCardinallyAdjacentPositions();
            ArrayList<Position> endNeighbours = new ArrayList<>();
            for (Position neighbourPos : endAdjPos) {
                int xPos = neighbourPos.getX();
                int yPos = neighbourPos.getY();
                if (xPos >= 0 && xPos <= width - 1 && yPos >= 0 && yPos <= height - 1) {
                    if (maze[yPos][xPos] == false) {
                        endNeighbours.add(neighbourPos);
                    }
                }
            }
            if (endNeighbours.size() > 0) {
                int neighbourIndex = (int) (Math.random() * endNeighbours.size());
                Position neighbour = endNeighbours.remove(neighbourIndex);
                maze[neighbour.getY()][neighbour.getX()] = true;
            }
        }
        return maze;
    }

    public ArrayList<Position> options(Position start, int width, int height, boolean[][] maze, boolean isEmpty) {
        ArrayList<Position> optionsList = new ArrayList<>();
        // Get all positions adjacent to start within a radius of 2
        // TODO: this adds only up,down,left,right. unsure if intented or have to make
        // an assumption
        int x = start.getX();
        int y = start.getY();
        optionsList.add(new Position(x, y - 2));
        optionsList.add(new Position(x + 2, y));
        optionsList.add(new Position(x, y + 2));
        optionsList.add(new Position(x - 2, y));

        ArrayList<Position> finalOptions = new ArrayList<>();
        for (Position pos : optionsList) {
            // Keep the ones that are walls and inside the boundry
            int xPos = pos.getX();
            int yPos = pos.getY();
            if (xPos >= 0 && xPos <= width - 1 && yPos >= 0 && yPos <= height - 1) {
                if (maze[yPos][xPos] == isEmpty) {
                    finalOptions.add(pos);
                }
            }
        }

        return finalOptions;
    }
}
