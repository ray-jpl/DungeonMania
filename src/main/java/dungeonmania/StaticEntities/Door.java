package dungeonmania.StaticEntities;
import dungeonmania.util.Position;

public class Door extends StaticEntity {
    private int keyId;

    /**
     * Constructor for Door
     * @param id
     * @param type
     * @param pos
     * @param keyId
     */
    public Door(String id, String type, Position pos, int keyId) {
        super(id, type, pos);
        this.keyId = keyId;
    }
    public int getKeyId() {
        return keyId;
    }

    public void setType(String type) {
        super.setType(type);
    }    
}
