package dungeonmania.StaticEntities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import dungeonmania.Entity;
import dungeonmania.GameMap;
import dungeonmania.MovingEntities.Spider;
import dungeonmania.Observers.TickObserver;
import dungeonmania.util.Position;

public class SpiderSpawner implements TickObserver {
    private Position spawn;
    private int spawnRate;

    public SpiderSpawner(Position spawn, int spawnRate) {
        this.spawn = spawn;
        this.spawnRate = spawnRate;
    }

    public void setSpawnRate(int spawnRate) {
        this.spawnRate = spawnRate;
    }

    public void update(int tick, GameMap map) {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        if (spawnRate == 0 || tick == 0) {
            return;
        } else if (tick % spawnRate == 0) {
            Entity spider = new Spider(UUID.randomUUID().toString(), "spider", spawn,
                    map.getConfigs().get("spider_health").getAsInt(), map.getConfigs().get("spider_attack").getAsInt());
            dungeonMap.computeIfAbsent(spawn, k -> new ArrayList<Entity>()).add(spider);
            map.setDungeonMap(dungeonMap);
        }
    }
}
