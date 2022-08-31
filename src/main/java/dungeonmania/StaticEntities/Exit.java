package dungeonmania.StaticEntities;

import java.util.List;
import java.util.Map;
import dungeonmania.Entity;
import dungeonmania.util.Position;

public class Exit extends StaticEntity {
    /**
     * Constructor for Exit
     * 
     * @param id
     * @param type
     * @param pos
     */
    public Exit(String id, String type, Position pos) {
        super(id, type, pos);
        super.setType("exit");
    }
}
