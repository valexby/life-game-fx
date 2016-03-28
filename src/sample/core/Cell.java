package sample.core;

/**
 * Created by valex on 23.3.16.
 */
public class Cell {
    private boolean state = false;
    private boolean newState;

    public Cell() {

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