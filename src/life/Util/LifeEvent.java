package life.Util;

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
}
