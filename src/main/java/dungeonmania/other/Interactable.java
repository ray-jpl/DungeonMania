package dungeonmania.other;

import dungeonmania.GameMap;
import dungeonmania.exceptions.InvalidActionException;

public interface Interactable {
    public void interact(GameMap map) throws InvalidActionException;

    public void oldInteract(GameMap map) throws InvalidActionException;
}
