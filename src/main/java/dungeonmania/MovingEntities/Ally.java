package dungeonmania.MovingEntities;

import dungeonmania.GameMap;

import dungeonmania.util.Position;

public class Ally extends MovingEntity {

    public Ally(String id, String type, Position pos, double health, double attack) {
        super(id, type, pos, health, attack);
        this.addUntraversable("wall");
        this.addUntraversable("boulder");
    }

    public void move(GameMap map) {
        this.moveToPlayer(map);
        if (this.getPos().equals(map.getPlayerPosition())) {
            this.move(map, map.getPlayerPreviousPosition());
        }
    }
}
