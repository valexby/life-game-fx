package life.Threads;

import life.core.Board;
import life.gui.Controller;

public abstract class AbstractControllerThread extends Thread {
    protected volatile Controller controller;

    public AbstractControllerThread(Controller controller) {
        this.controller = controller;
    }
}
