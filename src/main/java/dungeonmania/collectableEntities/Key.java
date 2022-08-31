package dungeonmania.collectableEntities;

import dungeonmania.util.Position;

public class Key extends CollectableEntity {
    private int keyId;
    public Key(String id, String type, Position pos, int keyId) {
        super(id, type, pos);
        this.keyId = keyId;
    }

    public int getKeyId() {
        return this.keyId;
    }

    public boolean useKey(String doorID) {
        if (this.getId().equals(doorID)) {
            return true;
        }
        return false;
    }
}
