package dungeonmania.MovingEntities;

import com.google.gson.JsonObject;

import dungeonmania.GameMap;

import dungeonmania.util.Position;

public class Hydra extends MovingEntity implements Hostile {

    private double health_increase_amount;
    private double health_increase_rate;

    public Hydra(String id, String type, Position pos, double health, double attack, JsonObject config) {
        super(id, type, pos, health, attack);
        this.addUntraversable("wall");
        this.addUntraversable("boulder");
        this.addUntraversable("active_bomb");
        this.addUntraversable("door");
        this.health_increase_amount = config.get("hydra_health_increase_amount").getAsDouble();
        this.health_increase_rate = config.get("hydra_health_increase_rate").getAsDouble();
    }

    public void move(GameMap map) {
        String effect = map.getPlayer().getCurrEffect();
        if (effect != null && effect.equals("invincibility_potion")) {
            this.moveAwayFromPlayer(map);
        } else {
            this.randomMove(map);
        }
    }

    public double health_increase_amount() {
        return this.health_increase_amount;
    }

    public double health_increase_rate() {
        return this.health_increase_rate;
    }
}
