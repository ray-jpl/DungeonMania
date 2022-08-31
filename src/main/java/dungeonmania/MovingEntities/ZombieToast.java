package dungeonmania.MovingEntities;

import dungeonmania.GameMap;
import dungeonmania.util.Position;

public class ZombieToast extends MovingEntity implements Hostile {

    public ZombieToast(String id, String type, Position pos, double health, double attack) {
        super(id, type, pos, health, attack);
        this.addUntraversable("wall");
        this.addUntraversable("boulder");
        this.addUntraversable("active_bomb");
        this.addUntraversable("door");
    }

    public void move(GameMap map) {
        String effect = map.getPlayer().getCurrEffect();
        if (effect != null && effect.equals("invincibility_potion")) {
            this.moveAwayFromPlayer(map);
        } else {
            this.randomMove(map);
        }
    }
}
