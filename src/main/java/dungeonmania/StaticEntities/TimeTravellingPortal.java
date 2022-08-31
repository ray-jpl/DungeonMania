package dungeonmania.StaticEntities;

import dungeonmania.util.Position;
import dungeonmania.GameMap;
import dungeonmania.MovingEntities.MovingEntity;
import dungeonmania.MovingEntities.Player;

public class TimeTravellingPortal extends StaticEntity {

    private final int TICKS_BACK = 30;

    public TimeTravellingPortal(String id, String type, Position pos) {
        super(id, type, pos);
    }

    public void interact(GameMap map, MovingEntity target) {
        // Entity blacklist
        if (!(target instanceof Player)) {
            return;
        }
        if (map.getTicksPassed() < TICKS_BACK) {
            map.rewind(map.getTicksPassed());
        } else {
            map.rewind(TICKS_BACK);
        }
    }
}
