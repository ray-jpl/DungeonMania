package dungeonmania.collectableEntities;

import dungeonmania.util.Position;

public class Sword extends CollectableEntity {
    private int sword_attack;
    private int durability;

    public Sword(String id, String type, Position pos, int sword_attack, int durability) {
        super(id, type, pos);
        this.sword_attack = sword_attack;
        this.durability = durability;
    }

    public int getSwordAttack() {
        return this.sword_attack;
    }

    public int getDurability() {
        return this.durability;
    }

    public void setSwordAttack(int attack) {
        this.sword_attack = attack;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }


    public void useSword() {
        this.durability -= 1;
    }

}
