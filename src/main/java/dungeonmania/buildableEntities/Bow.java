package dungeonmania.buildableEntities;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dungeonmania.util.Position;

public class Bow extends BuildableEntity{
    private int durability;
    private int dmgMultiplier = 2;
    public Bow(String id, String type, Position pos, int durability) {
        super(id, type, pos);
        this.durability = durability;

        List<HashMap<String,Integer>> blueprints = new ArrayList<HashMap<String,Integer>>();

        HashMap<String,Integer> blueprintOne = new HashMap<>();
        blueprintOne.put("wood", 1);
        blueprintOne.put("arrow", 3);
        blueprints.add(blueprintOne);
        this.setBlueprints(blueprints);
    }

    public int getDurability() {
        return this.durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getDmgMultiplier() {
        return this.dmgMultiplier;
    }

    public void useBow() {
        this.durability -= 1;
    }
}
