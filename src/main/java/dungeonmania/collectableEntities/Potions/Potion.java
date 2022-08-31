package dungeonmania.collectableEntities.Potions;

import dungeonmania.collectableEntities.CollectableEntity;
import dungeonmania.util.Position;

public abstract class Potion extends CollectableEntity {
    private int duration;

    public Potion(String id, String type, Position pos, int duration) {
        super(id, type, pos);
        this.duration = duration;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int dur) {
        this.duration = dur;
    }

    public void tick() {
        this.duration--;
    }
}
