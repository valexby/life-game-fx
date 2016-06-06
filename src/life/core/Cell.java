package life.core;

/**
 * Model of abstract cell
 */
public class Cell {
    private boolean state = false;
    private boolean newState;

    /**
     * Default constructor
     */
    public Cell() {

    }

    /**
     * Change state of cell to state
     * @param state
     */
    public void setNewState(boolean state) {
        newState = state;
    }

    /**
     * Change state of cell to opposite
     */
    public void updateState() {
        state = newState;
    }

    /**
     * Return cell state
     * @return cell state
     */
    public boolean getState() {
        return state;
    }
}