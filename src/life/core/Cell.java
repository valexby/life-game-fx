package life.core;

/**
 * Model of abstract cell
 */
public class Cell {
    private boolean state = false;
    private boolean newState;

    public Cell() {

    }

    public Cell(Cell clone) {
        state = clone.state;
        newState = clone.newState;
    }

    public Cell(boolean state) {
        this.state = state;
    }

    public void setNewState(boolean state) {
        newState = state;
    }

    public void updateState() {
        state = newState;
    }

    public boolean getState() {
        return state;
    }
}