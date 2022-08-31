package dungeonmania.collectableEntities.Bomb;

import com.google.gson.JsonObject;

import dungeonmania.collectableEntities.CollectableEntity;
import dungeonmania.util.Position;

public class Bomb extends CollectableEntity implements BombInterface{
    private int bombRadius;
    public Bomb(String id, String type, Position pos, JsonObject config) {
        super(id, type, pos);
        this.bombRadius = config.get("bomb_radius").getAsInt();
    }

    public int getBombRadius() {
        return this.bombRadius;
    }
}
