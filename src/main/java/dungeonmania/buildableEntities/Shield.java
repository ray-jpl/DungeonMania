package dungeonmania.buildableEntities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dungeonmania.util.Position;

public class Shield extends BuildableEntity{
    private int defence;
    private int durability;
    public Shield(String id, String type, Position pos, int defence, int durability) {
        super(id, type, pos);
        this.defence = defence;
        this.durability = durability;
        
        List<HashMap<String,Integer>> blueprints = new ArrayList<HashMap<String,Integer>>();

        HashMap<String,Integer> blueprintOne = new HashMap<>();
        blueprintOne.put("wood", 2);
        blueprintOne.put("treasure", 1);
        blueprints.add(blueprintOne);

        HashMap<String,Integer> blueprintTwo = new HashMap<>();
        blueprintTwo.put("wood", 2);
        blueprintTwo.put("key", 1);
        blueprints.add(blueprintTwo);

        this.setBlueprints(blueprints);
    }

    public int getDefence() {
        return this.defence;
    }

    public int getDurability() {
        return this.durability;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public void useShield() {
        this.durability -= 1;
    }
}
