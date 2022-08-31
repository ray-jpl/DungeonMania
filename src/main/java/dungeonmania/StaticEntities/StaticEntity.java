package dungeonmania.StaticEntities;

//import org.json.JSONObject;

import dungeonmania.Entity;
import dungeonmania.util.Position;
public abstract class StaticEntity extends Entity {
    /**
     * Constructor for StaticEntity
     * @param id
     * @param type
     * @param pos
     */
    public StaticEntity(String id, String type, Position pos) {
        super(id, type, pos);
    }
    

    

}