package dungeonmania;

import org.json.JSONObject;

import dungeonmania.util.Position;
import dungeonmania.MovingEntities.MovingEntity;

public class Entity implements EntityInterface {
    private String id;
    private Position pos;
    private String type;

    public Entity(String id, String type, Position pos) {
        this.id = id;
        this.pos = pos;
        this.type = type;
    }

    // Getters
    public String getId() {
        return this.id;
    }

    public Position getPos() {
        return pos;

    }

    public String getType() {
        return this.type;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void interact(GameMap map, MovingEntity source) {
    }

    // // Convert to an inventory object:
    // public JSONObject toJSONObjectInventory() {
    // JSONObject self = new JSONObject();
    // self.put("type", type);
    // return self;
    // }
    // Converts itself to a json object:
    public JSONObject toJSONObject() {
        JSONObject self = new JSONObject();
        self.put("x", pos.getX());
        self.put("y", pos.getY());
        self.put("type", getType());
        return self;
    }

    /**
     * Given a string type, checks if the entity is of the
     * same type as the one given.
     * 
     * @param eType
     * @return True is same type, false otherwise.
     */
    public boolean isType(String eType) {
        return (type.equals(eType));
    }

    /**
     * Given entity id, checks if "this" has the same id.
     * 
     * @param id
     * @return
     */
    public boolean hasId(String eId) {
        return (id.equals(eId));
    }

    public boolean isInteractable() {
        return false;
    }
}
