package life.util;

public class LifeEvent {
    public static final int TICK = 0, CLICK = 1, BOT = 2;
    int type, row, col, number;

    public LifeEvent(int type, int row, int col, int number) {
        this.type = type;
        this.row = row;
        this.col = col;
        this.number = number;
    }

    synchronized public int getType() {
        return type;
    }

    synchronized public int getRow() {
        return row;
    }

    synchronized public int getCol() {
        return col;
    }

    synchronized public int getNumber() {
        return number;
    }

    synchronized public void tick() {
        number--;
    }
}
