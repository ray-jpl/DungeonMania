package dungeonmania.MovingEntities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.google.gson.JsonObject;

import dungeonmania.Entity;
import dungeonmania.GameMap;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.other.Interactable;
import dungeonmania.util.Position;

public class Assassin extends MovingEntity implements Interactable, Hostile {
    private int bribe_radius;
    private int bribe_amount;
    private double fail_rate;
    private int recon_radius;

    public Assassin(String id, String type, Position pos, double health, double attack, JsonObject config) {
        super(id, type, pos, health, attack);
        this.addUntraversable("wall");
        this.addUntraversable("boulder");
        this.addUntraversable("active_bomb");
        this.addUntraversable("portal");
        this.addUntraversable("door");
        this.bribe_amount = config.get("assassin_bribe_amount").getAsInt();
        this.fail_rate = config.get("assassin_bribe_fail_rate").getAsDouble();
        this.recon_radius = config.get("assassin_recon_radius").getAsInt();
        this.bribe_radius = config.get("bribe_radius").getAsInt();
    }

    public void move(GameMap map) {
        String effect = map.getPlayer().getCurrEffect();
        if (effect != null && effect.equals("invincibility_potion")) {
            this.moveAwayFromPlayer(map);
        } else if (effect != null && effect.equals("invisibility_potion")
                && Position.distanceBetween(this.getPos(), map.getPlayerPosition()) > this.recon_radius) {
            this.randomMove(map);
        } else {
            this.moveToPlayer(map);
        }
    }

    public void interact(GameMap map) throws InvalidActionException {
        if (Position.distanceBetween(map.getPlayerPosition(), this.getPos()) > bribe_radius) {
            throw new InvalidActionException("Too far");
        }
        if (map.getPlayer().treasureAmount() < this.bribe_amount) {
            throw new InvalidActionException("Too broke");
        }
        map.getPlayer().useTreasure(map, this.bribe_amount);
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        Random rand = new Random();
        if (rand.nextDouble() < this.fail_rate) {
            map.setDungeonMap(dungeonMap);
            return;
        }
        Position pos = this.getPos();
        dungeonMap.get(pos).remove(this);
        if (dungeonMap.get(pos).size() == 0) {
            dungeonMap.remove(pos);
        }
        JsonObject config = map.getConfigs();
        Entity ally = new Ally(this.getId(), "assassin", pos,
                config.get("ally_defence").getAsDouble(), config.get("ally_attack").getAsDouble());
        dungeonMap.computeIfAbsent(pos, k -> new ArrayList<Entity>()).add(ally);
        map.setDungeonMap(dungeonMap);
    }

    public void oldInteract(GameMap map) throws InvalidActionException {
        HashMap<Position, ArrayList<Entity>> dungeonMap = map.getDungeonMap();
        Random rand = new Random();
        if (rand.nextDouble() < this.fail_rate) {
            map.setDungeonMap(dungeonMap);
            return;
        }
        Position pos = this.getPos();
        dungeonMap.get(pos).remove(this);
        if (dungeonMap.get(pos).size() == 0) {
            dungeonMap.remove(pos);
        }
        JsonObject config = map.getConfigs();
        Entity ally = new Ally(this.getId(), "assassin", pos,
                config.get("ally_defence").getAsDouble(), config.get("ally_attack").getAsDouble());
        dungeonMap.computeIfAbsent(pos, k -> new ArrayList<Entity>()).add(ally);
        map.setDungeonMap(dungeonMap);
    }

    @Override
    public boolean isInteractable() {
        return true;
    }
}
