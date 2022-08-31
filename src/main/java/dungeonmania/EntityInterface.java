package dungeonmania;

import dungeonmania.util.Position;


public interface EntityInterface {
    // Getters
    public String getId();
    public Position getPos();
    public String getType();
    // Setters
    public void setId(String id);
    public void setPos(Position pos);
    public void setType(String type);
}
