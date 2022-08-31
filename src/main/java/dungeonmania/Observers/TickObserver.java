package dungeonmania.Observers;

import dungeonmania.GameMap;

public interface TickObserver {
    public void update(int tick, GameMap map);
}
