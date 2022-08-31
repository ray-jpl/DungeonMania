package dungeonmania.buildableEntities;

import java.util.HashMap;
import java.util.List;

import dungeonmania.Entity;
import dungeonmania.util.Position;

public abstract class BuildableEntity extends Entity {
    private List<HashMap<String,Integer>> blueprints;

    public BuildableEntity(String id, String type, Position pos) {
        super(id, type, pos);
    }
    
    public List<HashMap<String,Integer>> getBlueprints() {
        return this.blueprints;
    }

    public void setBlueprints(List<HashMap<String,Integer>> blueprints) {
        this.blueprints = blueprints;
    }
}
