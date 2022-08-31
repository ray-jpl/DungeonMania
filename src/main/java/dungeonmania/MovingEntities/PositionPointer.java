package dungeonmania.MovingEntities;

import dungeonmania.util.Position;

public class PositionPointer {
    Position pos;
    PositionPointer head;

    public PositionPointer(Position pos, PositionPointer head) {
        this.pos = pos;
        this.head = head;
    }

    public Position get() {
        return this.pos;
    }

    public int getX() {
        return this.pos.getX();
    }

    public int getY() {
        return this.pos.getY();
    }

    public PositionPointer head() {
        return this.head;
    }

    public Boolean equals(PositionPointer x) {
        return x.get() == this.get();
    }

}
